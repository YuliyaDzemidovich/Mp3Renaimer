package local.yuliya.Mp3Renaimer;

import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

public class App {
    boolean isSubDirectoriesNeedToBeSearchedToo;
    String currentDirectory;
    File[] mp3filesFound;

    public App(){
        currentDirectory = System.getProperty("user.dir");
    }

    public static void main(String[] args){
        App app = new App();
        app.start();
    }

    private void start(){
        askSearchParametersFromUser();
        searchMp3Files();
        getMp3DataAndRenameMp3File();
    }

    // TODO: split method
    private void getMp3DataAndRenameMp3File(){
        if (mp3filesFound == null || mp3filesFound.length == 0) {
            System.out.println("No mp3 files found.");
            return;
        }

        Mp3File mp3file;
        String artist = null;
        String title = null;
        for (int i = 0; i < mp3filesFound.length; i++) {
            try {
                mp3file = new Mp3File(mp3filesFound[i]);
                if (mp3file.hasId3v2Tag()) { // first check for newer ID3v2 metadata format
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    System.out.println("INFO: ID3v2 song found");
                    System.out.println("Artist: " + id3v2Tag.getArtist());
                    System.out.println("Title: " + id3v2Tag.getTitle());
                    artist = id3v2Tag.getArtist();
                    title = id3v2Tag.getTitle();
                } else if (mp3file.hasId3v1Tag()) { // then check for older ID3v1 metadata format
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    System.out.println("INFO: ID3v1 song found");
                    System.out.println("Artist: " + id3v1Tag.getArtist());
                    System.out.println("Title: " + id3v1Tag.getTitle());
                    artist = id3v1Tag.getArtist();
                    title = id3v1Tag.getTitle();
                }
                if (artist != null && title != null) {
                    File renamedFile = new File(
                            artist + " - " + title + ".mp3");
                    mp3filesFound[i].renameTo(renamedFile);
                }
                artist = null;
                title = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void searchMp3Files(){
        File dir = new File(currentDirectory);
        FileFilter fileFilter = new WildcardFileFilter("*.mp3");
        mp3filesFound = dir.listFiles(fileFilter);
    }

    private void askSearchParametersFromUser(){
        System.out.println("Do you want to search in subdirectories too? Y/N");
        proceedUserAnswerIsSearchSubdirectories();
    }

    private void proceedUserAnswerIsSearchSubdirectories(){
        String input = "";
        Scanner in = new Scanner(System.in);
        while (true) {
            try {
                if (in.hasNext()) {
                    input = in.nextLine();
                    input.toLowerCase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isSetBooleanIsSubDirectoriesNeedToBeSearchedToo(input)) {
                break;
            } else {
                System.out.println("Please type in Y or N:");
            }
        }
    }

    private boolean isSetBooleanIsSubDirectoriesNeedToBeSearchedToo(
            String input){
        if (input.equals("y")) {
            isSubDirectoriesNeedToBeSearchedToo = true;
            return true;
        }
        if (input.equals("n")) {
            isSubDirectoriesNeedToBeSearchedToo = false;
            return true;
        } else {
            return false;
        }
    }
}
