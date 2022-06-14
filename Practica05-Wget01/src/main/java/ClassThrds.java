import java.net.URL;

public class ClassThrds implements Runnable{
    URL url;
    String dir;
    String name;
    public ClassThrds(URL url, String dir, String name){
        this.url=url;
        this.dir=dir;
        this.name=name;
    }
    @Override
    public void  run(){
        try {
            ClassWG.Download(url, dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
