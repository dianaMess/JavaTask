import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.codec.binary.Hex;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.ByteBuffer;
import java.util.*;
import java.lang.*;

class task {
    public static void main(String args[]){
        try {
            JSONParser parser = new JSONParser();
            FileReader hh = new FileReader("tickets.json");
            Path path = Paths.get("tickets.json");
            removeBom(path);
            Object a = parser.parse(new FileReader("tickets.json"));
            JSONObject obj = (JSONObject) a;
            JSONArray array = (JSONArray) obj.get("tickets");
            Iterator<JSONObject> iterator = array.iterator();
            int total_time = 0, count = 0, time = 0;
            ArrayList<Integer> times = new ArrayList<Integer>();
            while (iterator.hasNext()) {
                JSONObject jsonObjectInJsonArray = (JSONObject) iterator.next();
                String depart_time = jsonObjectInJsonArray.get("departure_time").toString(); 
                String arrive_time = jsonObjectInJsonArray.get("arrival_time").toString();
                String[] d_time = depart_time.split(":"); 
                String[] a_time = arrive_time.split(":");
                time = calculate_time(d_time, a_time);
                times.add(time);
                total_time += time;
                count++;
            }
            int average_time = total_time / count;
            Collections.sort(times);
            int min = average_time % 60;
            int hour = (average_time - min) / 60;
            System.out.printf("Среднее время полета: %d ч. %d мин.\n", hour, min);
            double n = (90 * count) / 100.0;
            int ind = (int) Math.ceil(n);
            Object[] time_array = times.toArray();
            int tmp = (int) time_array[ind - 1]; 
            min = tmp % 60;
            hour = tmp / 60;
            System.out.printf("90-й процентиль: %d ч. %d мин.\n", hour, min);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
     
    }
    private static int calculate_time(String[] d_time, String[] a_time) {
        int d_1 = Integer.parseInt(d_time[0]);
        int d_2 = Integer.parseInt(d_time[1]);
        int a_1 = Integer.parseInt(a_time[0]);
        int a_2 = Integer.parseInt(a_time[1]);
        int n_1, n_2;
        if (a_2 >= d_2) {
            n_2 = a_2 - d_2;
        } else {
            n_2 = (a_2 + 60) - d_2;
            a_1 -= 1;
        }
        n_1 = a_1 - d_1;
        return ((n_1 * 60) + n_2); 
    }
    private static boolean isContainBOM(Path path) throws IOException {
      if (Files.notExists(path)) {
          throw new IllegalArgumentException("Path: " + path + " does not exists!");
      }
      boolean result = false;
      byte[] bom = new byte[3];
      try (InputStream is = new FileInputStream(path.toFile())) {
          is.read(bom);
          String content = new String(Hex.encodeHex(bom));
          if ("efbbbf".equalsIgnoreCase(content)) {
              result = true;
          }
      }
      return result;
    }

    private static void removeBom(Path path) throws IOException {
      if (isContainBOM(path)) {
          byte[] bytes = Files.readAllBytes(path);
          ByteBuffer bb = ByteBuffer.wrap(bytes);
          System.out.println("Found BOM!");
          byte[] bom = new byte[3];
          bb.get(bom, 0, bom.length);
          byte[] contentAfterFirst3Bytes = new byte[bytes.length - 3];
          bb.get(contentAfterFirst3Bytes, 0, contentAfterFirst3Bytes.length);
          Files.write(path, contentAfterFirst3Bytes);
      } else {
          System.out.println("This file doesn't contains UTF-8 BOM!");
      }

  }

}
