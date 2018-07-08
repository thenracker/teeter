package cz.uhk.teeter;

public final class UnitsHelper {
    public static float pixelsToMeters(int pixelsCount, int density) {
        return ((float) pixelsCount / (float) density / 39f); //39 - to jest převod z palců na metr .. protože density je v PPI - pixels per inch
    }

    public static int metersToPixels(float metersCount, int density) {
        return (int) (metersCount * 39f * (float) density);
    }
}
