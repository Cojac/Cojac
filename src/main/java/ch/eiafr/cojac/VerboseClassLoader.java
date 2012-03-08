package ch.eiafr.cojac;

// java -Djava.system.class.loader=ch.eiafr.cojac.VerboseClassLoader

public class VerboseClassLoader extends ClassLoader {
    public VerboseClassLoader(ClassLoader c) {
        super(c);
    }
        
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println("VerboseClassLoader loading " +name +" "+resolve);
        System.out.flush();
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("VerboseClassLoader finding " +name);
        System.out.flush();
        return super.findClass(name);
    }
    
    
}
