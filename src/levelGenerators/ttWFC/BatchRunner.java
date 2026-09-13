package levelGenerators.ttWFC;

import engine.core.MarioGame;
import engine.core.MarioLevelModel;
import engine.core.MarioResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class BatchRunner {
    public static void main(String[] args) throws Exception {
        run(new String[] {"all"}, new int[] {1}, new int[] {16}, 1000, 10000);
    }

    public static void run(String[] samples, int[] Ms, int[] Ns, int repeats,
            int attemptsPerRepeat) throws Exception {
        run(samples, createWindowSizes(Ms, Ns), repeats, attemptsPerRepeat,
            Paths.get("output"), false);
        }

        public static void run(String[] samples, int[][] windowSizes, int repeats,
            int attemptsPerRepeat, Path outputRoot, boolean separateWindowFolders)
            throws Exception {
        Files.createDirectories(outputRoot);
        List<String> lines;
        Random rnd = new Random(12345);
        for (String sample : samples) {
                if(sample.equals("all")){
                    lines = Files.readAllLines( Paths.get("src/levelGenerators/ttWFC/samples/" + "lvl-1" + ".txt"));
                }
                else{
                    lines = Files.readAllLines( Paths.get("src/levelGenerators/ttWFC/samples/" + sample + ".txt") );
                }
                

                for (int[] windowSize : windowSizes) {
                    if (windowSize.length != 2) {
                        throw new IllegalArgumentException("Each window must contain M and N");
                    }
                    int M = windowSize[0];
                    int N = windowSize[1];
                    Path outputDirectory = separateWindowFolders
                        ? outputRoot.resolve(M + "x" + N)
                        : outputRoot;
                    Files.createDirectories(outputDirectory);
                        int outW = lines.get(0).length();
                        int outH  = 16;
                        if(outW%M>0) outW += M - (outW % M);
                        if(outH%N>0) outH += N - (outH % N);    
                        for (int r = 0; r < repeats; r++) {
                            boolean success = false;
                            int seed = -1;
                            OverlappingModel wfc = null;

                            for (int attempt = 0; attempt < attemptsPerRepeat; attempt++) {
                                seed = rnd.nextInt();
                                wfc = new OverlappingModel(
                                  sample, M, N, outW / M, outH / N,
                                  false, false, 1, true,
                                  OverlappingModel.Heuristic.Entropy
                                );
                                if (wfc.Run(seed, -1)) {
                                    success = true;
                                    break;
                                }
                            }

                            if (!success) {
                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                System.out.println("   AFTER " + attemptsPerRepeat + "ATTEMPTS WFC FAILED ON " + sample+ " ON Window M="+M+", N="+N);
                                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                continue;
                            }

                            String tmp = outputDirectory.resolve(
                                "tmp-" + sample + "-M" + M + "-N" + N + "-s" + seed
                            ).toString();
                            if(success) wfc.Save(tmp);

                            // MarioLevelModel model = new MarioLevelModel(outW, outH);
                            // model.copyFromString(new String(
                            //   Files.readAllBytes(Paths.get(tmp + ".txt"))
                            // ));
                            // MarioResult res = game.runGame(
                            //   new agents.robinBaumgarten.Agent(),
                            //   model.getMap(),
                            //   40, 0, true
                            // );

                            // System.out.println("****************************************************************");
                            // System.out.println("**SAMPLE: " + sample + ", M: " + M + ", N: " + N + "********");
                            // System.out.println("****************************************************************");
                            // System.out.println("Game Status: " + res.getGameStatus().toString() +" Percentage Completion: " + res.getCompletionPercentage());
                            // System.out.println("Lives: " + res.getCurrentLives() + " Coins: " + res.getCurrentCoins() + " Remaining Time: " + (int) Math.ceil(res.getRemainingTime() / 1000f));
                            // System.out.println("Mario State: " + res.getMarioMode() +" (Mushrooms: " + res.getNumCollectedMushrooms() + " Fire Flowers: " + res.getNumCollectedFireflower() + ")");
                            // System.out.println("Total Kills: " + res.getKillsTotal() + " (Stomps: " + res.getKillsByStomp() + " Fireballs: " + res.getKillsByFire() + " Shells: " + res.getKillsByShell() + " Falls: " + res.getKillsByFall() + ")");
                            // System.out.println("Bricks: " + res.getNumDestroyedBricks() + " Jumps: " + res.getNumJumps() + " Max X Jump: " + res.getMaxXJump() + " Max Air Time: " + res.getMaxJumpAirTime());
                            // System.out.println("****************************************************************");
                        }
                }
            }

        System.out.println("Batch finished. Generated levels are in " + outputRoot);
    }

    private static int[][] createWindowSizes(int[] Ms, int[] Ns) {
        int[][] windowSizes = new int[Ms.length * Ns.length][2];
        int index = 0;
        for (int M : Ms) {
            for (int N : Ns) {
                windowSizes[index++] = new int[] {M, N};
            }
        }
        return windowSizes;
    }
}
