/**sudo ifconfig eno1 inet6 add 2001::1234:1 */
#include <fcntl.h>
#include <stdio.h> // 
#include <string.h>  //
#include <unistd.h>  //  
#include <netinet/in.h> // 
#include <sys/types.h> //  
#include <sys/socket.h>  //
#include <stdlib.h>//exit
#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#define puerto "1234"  
#define MAXBUF 1024  

void error(const char * msj){
 perror(msj);
 exit (1);
}//error

typedef struct CLIENT {  
    int fd;  
    struct sockaddr_storage addr;      
}CLIENT;  
  
/*************************** 
**server for multi-client  
**PF_SETSIZE=1024 
****************************/  
int main(int argc, char** argv)  
{  
 FILE *f;
 int sd,cd,n,n1,v=1,rv,op=0,i,nready,maxi = -1,sockfd,maxfd=-1;   
 socklen_t ctam;
 char hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
 struct addrinfo hints, *servinfo, *p;
 struct sockaddr_storage cdir; // connector's address 
 ctam= sizeof(cdir);
 struct timeval tv;
 fd_set a1,a,b1,b;   
 char *buf = "Un mensaje usando socket no bloqueante..";
 CLIENT client[FD_SETSIZE];  
 memset(&hints, 0, sizeof (hints));  //indicio
 hints.ai_family = AF_INET6;    /* Allow IPv4 or IPv6  familia de dir*/
 hints.ai_socktype = SOCK_STREAM;
 hints.ai_flags = AI_PASSIVE; // use my IP
 hints.ai_protocol = 0;          /* Any protocol */
 hints.ai_canonname = NULL;
 hints.ai_addr = NULL;
 hints.ai_next = NULL;

 if ((rv = getaddrinfo(NULL, puerto, &hints, &servinfo)) != 0) {
     fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
     return 1;
 }//if

    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
            perror("server: socket");
            continue;
        }//if

        if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &v,sizeof(int)) == -1) {
            perror("setsockopt");
            exit(1);
        }//if

	if (setsockopt(sd, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&op, sizeof(op)) == -1) {
            perror("setsockopt   no soporta IPv6");
            exit(1);
        }//if

        if (bind(sd, p->ai_addr, p->ai_addrlen) == -1) {
            close(sd);
            perror("server: bind");
            continue;
        }//if

        break;
    }//for

    freeaddrinfo(servinfo); // all done with this structure
    if (p == NULL)  {
        fprintf(stderr, "servidor: error en bind\n");
        exit(1);
    }//if
    int flags = fcntl(sd, F_GETFL, 0);
    fcntl(sd, F_SETFL, flags | O_NONBLOCK);

  if (listen(sd, FD_SETSIZE) == -1) {  
            perror("listen");  
            exit(1);  
        }//if  
    for(i=0;i<FD_SETSIZE;i++)  
    {  
        client[i].fd = -1;  
    }//for  
  
    FD_ZERO(&a);
    FD_ZERO(&a1);
    FD_ZERO(&b);
    FD_ZERO(&b1);             
    FD_SET(sd, &a);    
    maxfd = sd;    
    printf("Servidor listo para recibir un m�ximo de %d conexiones...\n",FD_SETSIZE);      
     while (1){         
        a1 = a;
	b1 = b;            
        tv.tv_sec = 1;  
        tv.tv_usec = 0;  
        nready = select(maxfd + 1, &a1, &b1, NULL, &tv);  
        if(nready == 0)  
          continue;  
        else if(nready < 0){  
            printf("error en funcion select()!\n");  
            break;  
        }else{  
            if(FD_ISSET(sd,&a1)) // nueva conexion  
            {             
                if((cd = accept(sd,(struct sockaddr *)&cdir,&ctam)) == -1)  
                {  
                    perror("accept() error\n");  
                    continue;  
                }//if  
		f = fdopen(cd,"w+");
                for(i=0;i<FD_SETSIZE;i++)  
                {  
                    if(client[i].fd < 0)  
                    {  
                     struct linger linger;
                     linger.l_onoff = 1;
                     linger.l_linger = 30;
                     if(setsockopt(cd,SOL_SOCKET, SO_LINGER,(const char *) &linger,sizeof(linger))==-1){
                        perror("setsockopt(...,SO_LINGER,...)");
                     }//if    
                     client[i].fd = cd;  
                     client[i].addr = cdir;      
                     if (getnameinfo((struct sockaddr *)&cdir, sizeof(cdir), hbuf, sizeof(hbuf),sbuf,sizeof(sbuf), NI_NUMERICHOST | NI_NUMERICSERV) == 0){
                            printf("cliente conectado desde %s:%s\n", hbuf,sbuf);}//if
                        break;  
                    }//if  
                }//for  
                if(i == FD_SETSIZE)           
                    printf("Demasiadas conexiones");           
                FD_SET(cd,&b);  
                if(cd > maxfd)  
                    maxfd = cd;  
                if(i > maxi)  
                    maxi = i;  
            }else{             
                 for(i=0;i<=maxi;i++){             
                    if((sockfd = client[i].fd)<0)  
                        continue;                 
                    if(FD_ISSET(sockfd,&b1))  
                    {                         
                        if((n = write(sockfd,buf,strlen(buf)+1)) > 0)  
                        {  
		            fflush(f);
			    printf("%d bytes enviados\nMensaje enviado a %s: %s\n",n,hbuf,buf); 
			    close(sockfd);
	   		    fclose(f);  
                            FD_CLR(sockfd,&b);  
                            client[i].fd = -1;  
    			    continue;   
                        }                 
                        else  
                        {  
                            printf("Cliente cerro su conexion!\n");  
                            close(sockfd);  
                            FD_CLR(sockfd,&b);  
                            client[i].fd = -1;
			    fclose(f);
			    continue;  
                        }//else  
                    }//if  
                }//for  
            }//else  
        }     
    }//while  
    close(sd);  
}//main  
