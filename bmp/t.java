

public class t{

    private byte[] getPixelArrayToBmpByteArray(byte[] pixelData, int width, int height, int depth) throws Exception{
        int[] pixels = byteToInt(pixelData);

        BufferedImage image = null;
        if(depth == 8) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        else if(depth == 24){
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }

        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0, 0, width, height, pixels);
        image.setData(raster);
        return getBufferedImageToBmpByteArray(image);
    }

    private byte[] getBufferedImageToBmpByteArray(BufferedImage image) {
        byte[] imageData = null;
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            ImageIO.write(image, "bmp", bas);
            imageData = bas.toByteArray();
            bas.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageData;
    }

    private int[] byteToInt(byte[] data) {
        int[] ints = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            ints[i] = (int) data[i] & 0xff;
        }
        return ints;
    } 


}