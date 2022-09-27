package engine;

object Engine {
  val UNLIMITED: Int = -1
}

// This class manages the main thread that the game is running on
class Engine(val gameInterface: GameInterface) extends Runnable, EngineInterface {

    private var running = false
    private var printFps = false
    private var desiredTPS = -1
    private var desiredFPS = -1
    private var currentTPS: Int = 0
    private var currentFPS: Int = 0

    // Called when a new thread is created. This should not be called manually
    override def run(): Unit = {
        gameInterface.init(this)

        var fps = 0
        var tps = 0

        // Calculate how much time is between each render/update
        val nsPerTick = if desiredTPS == 0 then 0 else 1000000000d / desiredTPS
        val nsPerFrame = if desiredFPS == 0 then 0 else 1000000000d / desiredFPS

        var before = System.nanoTime()
        var now = 0l
        var unprocessedTicks = 0d
        var unprocessedFrames = 0d

        var fpsTimer = System.currentTimeMillis()

        while running do {

            now = System.nanoTime();
            unprocessedTicks += (now - before) / nsPerTick
            unprocessedFrames += (now - before) / nsPerFrame
            before = now;

            if unprocessedTicks >= 1 then {
                gameInterface.update()
                tps += 1
                unprocessedTicks -= Math.floor(unprocessedTicks)
            }

            if (desiredFPS == Engine.UNLIMITED || unprocessedFrames >= 1) {
                gameInterface.render()
                fps += 1
                unprocessedFrames -= Math.floor(unprocessedFrames)
            }

            if (System.currentTimeMillis() - fpsTimer >= 1000) {
                currentFPS = fps;
                currentTPS = tps;

                if(printFps) {
                    System.out.println(s"FPS: $fps TPS: $tps");
                }

                fpsTimer = System.currentTimeMillis();
                fps = 0;
                tps = 0;
            }

            Thread.sleep(2)
        }
    }

    override def start(): Unit = {
        running = true;
        // Create a new thread and start it
        new Thread(this).run();
    }

    override def stop(): Unit = {
        running = false;
    }

    override def setDesiredTPS(tps: Int): Unit = {
        desiredTPS = tps;
    }

    override def setDesiredFPS(fps: Int): Unit = {
        desiredFPS = fps;
    }

    override def printFps(print: Boolean): Unit = {
        printFps = print;
    }

    override def getFPS(): Int = currentFPS

    override def getTPS(): Int = currentTPS
}
