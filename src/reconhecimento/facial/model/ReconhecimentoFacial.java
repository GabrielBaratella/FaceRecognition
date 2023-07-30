package reconhecimento.facial.model;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.*;
import javax.swing.*;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.*;
import javax.swing.*;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_SIMPLEX;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

public class ReconhecimentoFacial {

    public static void main(String[] args) {
        Loader.load(opencv_objdetect.class);

        // Inicializar o classificador de faces pré-treinado
        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("src\\resources\\haarcascade_frontalface_alt.xml"); // Certifique-se de ter o arquivo XML correto

        // Inicializar a captura de vídeo da câmera
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // 0 para a primeira câmera, 1 para a segunda e assim por diante
        try {
            grabber.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CanvasFrame canvas = new CanvasFrame("Detecção de Faces");
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (true) {
            // Capturar o frame da câmera
            try {
                Thread.sleep(500); // Adicionar um atraso de 100 milissegundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Frame frame;
            try {
                frame = grabber.grab();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            // Converter o frame para uma matriz do OpenCV (Mat)
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            Mat imagemColorida = converter.convert(frame);

            // Converter a imagem para escala de cinza para detecção de faces
            Mat imagemGrayScale = new Mat();
            cvtColor(imagemColorida, imagemGrayScale, COLOR_BGRA2GRAY);

            // Detectar as faces no frame
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(imagemGrayScale, faces, 1.1, 3, 0, new Size(150, 150), new Size(500, 500));

            // Desenhar retângulos ao redor das faces detectadas e adicionar texto
            for (int i = 0; i < faces.size(); i++) {
                Rect rect = faces.get(i);
                rectangle(imagemColorida, rect.tl(), rect.br(), new Scalar(0, 255, 0, 0));

                // Adicionar texto "Rosto Reconhecido!" ao lado do retângulo
                String texto = "Rosto Reconhecido!";
                Point pontoTexto = new Point(rect.x() + rect.width(), rect.y() - 10);
                putText(imagemColorida, texto, pontoTexto, FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255, 0));
            }

            // Exibir o frame com as faces detectadas
            canvas.showImage(converter.convert(imagemColorida));

            // Salvar imagem com os rostos detectados
            String nomeArquivo = "rostos_detectados.jpg";
            imwrite(nomeArquivo, imagemColorida);
            System.out.println("Imagem com rostos detectados salva como " + nomeArquivo);
        }

        // Liberar recursos
        try {
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
