package Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
	
	public static void saveBytes(Path path, byte[] bytes) {
		try (FileOutputStream fos = new FileOutputStream(path.toString())) {
			   fos.write(bytes);
			   //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static byte[] readBytes(Path path) {
		byte[] array = null;
		try {
			array = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}
	
	public static ArrayList<String> read(Path path) {
		ArrayList<String> arrList = new ArrayList<String>();
		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(path)) {

			stream.forEach(System.out::println);
			arrList = getArrayListFromStream(stream);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return arrList;
	     
	}
	
	public static <T> ArrayList<T> 
    getArrayListFromStream(Stream<T> stream) 
    { 
  
        // Convert the Stream to List 
        List<T> 
            list = stream.collect(Collectors.toList()); 
  
        // Create an ArrayList of the List 
        ArrayList<T> 
            arrayList = new ArrayList<T>(list); 
  
        // Return the ArrayList 
        return arrayList; 
    } 
}
