
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.imageio.*;

//class do do steganography on given image
class StegoEncoder {

    //static function to calculate current bit
    private static int calcBit(byte[] byteText, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        return (byteText[byteIndex] >> bitPos) & 1;
    }

    public static void main(String[] args) {
        String textToEncode = "Oda do Labu z C++ Goncerz wchodzi i mówi, że zadanie jest proste, student patrzy w konsolę i nie chce mu się spać, w głowie wskaźniki krążą jak coś bardzo ostre, i na cały korytarz chciałby głośno klać. Rule of Five prześladuje go w środku tej nocy, destruktory i kopie konstruktora się śmieją, a student traci powoli ostatnie swoje mocy, bo zadania 'trywialne' przez ekran mu świeją. Goncerz wzrusza ramiony i mówi: mało czasu? student chciałby odpowiedzieć, lecz milczy grzecznie, zadanie banalnie — to filozofia hałasu, i znowu noc zarwana, choć chciałby spać bezpiecznie. I tak co dwa tygodnie ten sam powtarza się temat, C++ nie lituje — jest twardy jak głaz, Goncerz się uśmiecha, to przecież jego klimat, a student czeka na cud… może nadejdzie czas.";
        BufferedImage img;

        try {
            File mainImg = new File("tomek.jpg");
            img = ImageIO.read(mainImg);
        } catch (IOException e) {
            System.err.println("Nie udało się wczytać obrazu: " + e.getMessage());
            return;
        }

        byte[] byteText = textToEncode.getBytes(StandardCharsets.UTF_8);
        int length = byteText.length;

        System.out.println(length);

        byte[] header = new byte[4];
        header[0] = (byte) (length >> 24);
        header[1] = (byte) (length >> 16);
        header[2] = (byte) (length >> 8);
        header[3] = (byte) (length);

        byte[] payload = new byte[4 + byteText.length];
        System.arraycopy(header, 0, payload, 0, 4);
        System.arraycopy(byteText, 0, payload, 4, byteText.length);

        int bitIndex = 0;
        int totalBits = payload.length * 8;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (bitIndex >= totalBits) {
                    break;
                }
                Color pixel = new Color(img.getRGB(x, y));
                int r = pixel.getRed();
                int g = pixel.getGreen();
                int b = pixel.getBlue();

                if (bitIndex < totalBits) {
                    r = (r & 0xFE) | calcBit(payload, bitIndex++);
                }
                if (bitIndex < totalBits) {
                    g = (g & 0xFE) | calcBit(payload, bitIndex++);
                }
                if (bitIndex < totalBits) {
                    b = (b & 0xFE) | calcBit(payload, bitIndex++);
                }

                img.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        try {
            ImageIO.write(img, "png", new File("encoded.png"));
            System.out.println("Zapisano wynik.png");
        } catch (IOException e) {
            System.err.println("Nie udało się zapisać: " + e.getMessage());
        }

        System.out.println("Szerokość: " + img.getWidth());
    }
}
