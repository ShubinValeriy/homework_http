import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


import java.io.*;

public class Main {
    public static ObjectMapper mapper = new ObjectMapper();
    public static final String URI_NASA = "https://api.nasa.gov/planetary/apod?" +
            "api_key=mXzWf3h7Ye7xispFsEDG9uQVy4xRk8E0yGOkG3gp";

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(URI_NASA);
        try {
            CloseableHttpResponse response = httpClient.execute(request);

            NatoInfo natoInfo = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
            });
            // Получаем URL для скачивания медиа
            String urlMedia = natoInfo.getUrl();
            // Получаем имя медиа файла
            String fileName = urlMedia.substring(urlMedia.lastIndexOf("/") + 1);
            // Создаем новый объект HttpGet
            HttpGet requestMedia = new HttpGet(urlMedia);
            // Извлекаем объект ответа
            CloseableHttpResponse responseMedia = httpClient.execute(requestMedia);
            // Возвращаем тело ответа
            HttpEntity entity = responseMedia.getEntity();
            // Преобразуем тело ответа в массив Байт
            byte[] byteArray = EntityUtils.toByteArray(entity);
            // Создаем файл для записи медиа
            File saveFile = new File(fileName);
            //Записываем массив байт в созданный файл
            try (FileOutputStream fos = new FileOutputStream(saveFile);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                bos.write(byteArray, 0, byteArray.length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
