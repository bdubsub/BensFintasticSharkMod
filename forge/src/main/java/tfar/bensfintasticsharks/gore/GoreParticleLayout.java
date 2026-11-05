package tfar.bensfintasticsharks.gore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Finds camera-plane grid positions that keep square billboard particles apart. */
public final class GoreParticleLayout {
    private GoreParticleLayout() {
    }

    public static List<Offset> separatedOffsets(int count, double centerHalfSize, double particleHalfSize,
                                                 double gap, double phase) {
        if (count < 0 || !Double.isFinite(centerHalfSize) || centerHalfSize < 0.0D
                || !Double.isFinite(particleHalfSize) || particleHalfSize < 0.0D
                || !Double.isFinite(gap) || gap < 0.0D || !Double.isFinite(phase)) {
            throw new IllegalArgumentException("Particle layout values must be finite and non-negative.");
        }
        if (count == 0) return List.of();
        if (count == 1 && centerHalfSize == 0.0D) return List.of(new Offset(0.0D, 0.0D));

        double spacing = 2.0D * particleHalfSize + gap;
        if (spacing == 0.0D && (count > 1 || centerHalfSize > 0.0D)) {
            throw new IllegalArgumentException("Separated particles require positive size or gap.");
        }
        double centerClearance = centerHalfSize + particleHalfSize + gap;
        int quarterTurns = Math.floorMod((int) Math.floor(phase / (Math.PI * 0.5D)), 4);
        List<Offset> offsets = new ArrayList<>(count);

        for (int ring = 1; offsets.size() < count; ring++) {
            List<Offset> candidates = squareRing(ring, spacing, quarterTurns);
            candidates.removeIf(offset -> Math.max(Math.abs(offset.right), Math.abs(offset.up))
                    < centerClearance);
            candidates.sort(Comparator.comparingDouble(offset -> Math.atan2(offset.up, offset.right)));

            int needed = count - offsets.size();
            if (candidates.size() <= needed) {
                offsets.addAll(candidates);
                continue;
            }
            for (int index = 0; index < needed; index++) {
                int candidateIndex = (int) ((long) index * candidates.size() / needed);
                offsets.add(candidates.get(candidateIndex));
            }
        }

        return List.copyOf(offsets);
    }

    private static List<Offset> squareRing(int ring, double spacing, int quarterTurns) {
        List<Offset> offsets = new ArrayList<>(ring * 8);
        for (int x = -ring; x <= ring; x++) addRotated(offsets, x, -ring, spacing, quarterTurns);
        for (int y = -ring + 1; y <= ring; y++) addRotated(offsets, ring, y, spacing, quarterTurns);
        for (int x = ring - 1; x >= -ring; x--) addRotated(offsets, x, ring, spacing, quarterTurns);
        for (int y = ring - 1; y > -ring; y--) addRotated(offsets, -ring, y, spacing, quarterTurns);
        return offsets;
    }

    private static void addRotated(List<Offset> offsets, int x, int y, double spacing, int quarterTurns) {
        int rotatedX = switch (quarterTurns) {
            case 1 -> -y;
            case 2 -> -x;
            case 3 -> y;
            default -> x;
        };
        int rotatedY = switch (quarterTurns) {
            case 1 -> x;
            case 2 -> -y;
            case 3 -> -x;
            default -> y;
        };
        offsets.add(new Offset(rotatedX * spacing, rotatedY * spacing));
    }

    public record Offset(double right, double up) {
    }
}
