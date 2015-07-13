package com.sunshineapps.rift.experimental;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Sys;

public final class Client implements ClientCallback {
    private RiftWindow rift;
    private MirrorWindow mirrorWindow;
    private final FPSCounter fpsCounter = new FPSCounter();
    private final Scene scene2 = new TestGlobeScene();
    private final Scene scene3 = new RoomScene();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicBoolean renderScene2 = new AtomicBoolean(true);
    private final AtomicBoolean renderScene3 = new AtomicBoolean(false);
    
    private float angle = 0.0f;
    
    public void run() {
        System.out.println("Java "+System.getProperty("java.version"));
        System.out.println("LWJGL " +Sys.JNI_LIBRARY_NAME+"-"+Sys.getVersion());
        
        // step 0 - create rift
        System.out.println("step 0 - create rift");
        rift = new RiftWindow(this);
     
        // step 8 - create mirror window
        System.out.println("step 8 - create mirror window");
        mirrorWindow = new MirrorWindow(this, 30, rift.getPixelWidth()/2, rift.getCanvasRatio());         // Our mirror window can be smaller and updated less frequently than the rift
        mirrorWindow.setCyclopsMode(true);                                                          // We only need one eye...

        // display
        try {
            rift.init();
            mirrorWindow.init(rift.getMirrorTexture(mirrorWindow.getWindowW(), mirrorWindow.getWindowH()));
      //      mouse.init();
            scene2.init();
            scene3.init();
            fpsCounter.init();
            while (shutdown.get() == false) {
                rift.render();
                mirrorWindow.render();
                fpsCounter.frameDone();
            }
        } finally {
            fpsCounter.shutdown();
            mirrorWindow.close();
            rift.shutdown();
        } 
    }
    
    @Override
    public void drawScene(final Matrix4f mat) {
        if (renderScene2.get()) {
            mat.translate(new Vector3f(0.0f, 0.0f, -130.0f)); 
            angle += .05;
            mat.rotate(angle, new Vector3f(0.0f, 1.0f, 0.0f));
            scene2.render();
        }
        if (renderScene3.get()) {
            scene3.render();
        }
    }
    
    @Override
    public void keyPressed(final int key) {
        if (key == '2') {
            renderScene2.set(!renderScene2.get());
        }
        if (key == '3') {
            renderScene3.set(!renderScene3.get());
        }
    }

    @Override
    public void shutdown() {
        shutdown.set(true);
    }
    
    public static void main(String[] args) {
        new Client().run();
    }

}