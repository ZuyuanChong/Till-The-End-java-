package TillTheEnd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
public class TillTheEnd {
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        Normal normal = new Normal();
        Hard hard = new Hard();
        Season season = new Season();
        Tax tax = new Tax();
        Dragon dragon = new Dragon();
        Citizen citizen = season.citizen;
        Tower tower = season.tower;
        Wall wall = season.wall;
        int difficulty = 0;
        
        System.out.println("                __        _                |\\             //  ");
        System.out.println("              _/  \\    _(\\(o               \\\\           _!_");
        System.out.println("             /     \\  /  _  ^^^o            \\\\         /___\\ ");
        System.out.println("            /   !   \\/  ! '!!!v'             \\\\        [+++]");
        System.out.println("          !  !  \\ _' ( \\____                 \\\\    _ _\\^^^/_ _");
        System.out.println("          ! . \\ _!\\   \\===^\\)                 \\\\/ (    '-'  ( )");
        System.out.println("            \\ \\_!  / __!                       /( \\/ | {&}   /\\ \\");
        System.out.println("             \\!   /    \\                         \\  / \\     / _> )");
        System.out.println("       (\\_      _/   _\\ )                         \"`   >:::;-'`\"\"'-.");
        System.out.println("        \\ ^^--^^ __-^ /(__                              /:::/         \\");
        System.out.println("        ^^----^^    \"^--v'                             /  /||   {&}   |");
        System.out.println("                                                      (  / (\\         /");
        System.out.println("                                                      / /   \\'-.___.-'");
        System.out.println("                                                    _/ /     \\ \\");
        System.out.println("                                                    /___|    /___|");
        
        try{
           FileInputStream fileInputStream = new FileInputStream("Dragon.mp3"); 
           Player player = new Player(fileInputStream);
           player.play();
           System.out.println("Welcome to Till The End - A Tower Defense Game!");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(JavaLayerException e){
            e.printStackTrace();
        }
        
        System.out.println("\nWould you like to start a NEW GAME or CONTINUE?");
        System.out.println("\n1. CONTINUE");
        System.out.println("2. NEW GAME\n");
        System.out.print("Please enter your command: ");
        int input = in.nextInt();
        while( !(input==1 || input==2)){
            System.out.println("You enter an invalid command.");
            System.out.print("Please enter you command: ");
            input = in.nextInt();
        }
        
        switch(input){
            case 1:
                /* CONTINUE */
                // read file
                File encryptedata = new File("encrypt.txt");
                File decryptedata = new File("decrypt.txt");
                
                try{
                    encryptDecrypt("12345678", Cipher.DECRYPT_MODE, encryptedata, decryptedata);
                } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IOException e){
                    e.printStackTrace();
                }
                
                try{
                    System.out.println("");
                    System.out.println("Reading saved file...");
                    System.out.println("... ...... ...");
                    BufferedReader br = new BufferedReader(new FileReader("decrypt.txt"));
                
                    //Season - time
                    int time = Integer.parseInt(br.readLine());
                    season.setTime(time);
                    
                    //Tax - current_gold
                    int current_gold = Integer.parseInt(br.readLine());
                    tax.setCurrent_gold(current_gold);
                
                    //Tower - attackpoint, critical chance, accuracy
                    int tower_ap = Integer.parseInt(br.readLine());
                    int tower_cc = Integer.parseInt(br.readLine());
                    int tower_acc = Integer.parseInt(br.readLine());
                    tower.setAttackpoint(tower_ap);
                    tower.setCriticalChance(tower_cc);
                    tower.setAccuracy(tower_acc);
                
                    //Wall - healthpoint, block percentage
                    int wall_hp = Integer.parseInt(br.readLine());
                    int wall_bp = Integer.parseInt(br.readLine());
                    wall.setHealthpoint(wall_hp);
                    wall.setBlockPercentage(wall_bp);
                
                    //Citizen - emotional, nervous, lazy, berserk, diligent, fearless
                    int emotional = Integer.parseInt(br.readLine());
                    int nervous = Integer.parseInt(br.readLine());
                    int lazy = Integer.parseInt(br.readLine());
                    int berserk = Integer.parseInt(br.readLine());
                    int diligent = Integer.parseInt(br.readLine());
                    int fearless = Integer.parseInt(br.readLine());
                    citizen.setEmotional(emotional);
                    citizen.setNervous(nervous);
                    citizen.setLazy(lazy);
                    citizen.setBerserk(berserk);
                    citizen.setDiligent(diligent);
                    citizen.setFearless(fearless);
                
                    //Dragon - level, healthpoint, attaackpoint, critical chance, accuracy, damage, full healthpoint
                    int dragon_level = Integer.parseInt(br.readLine());
                    int dragon_hp = Integer.parseInt(br.readLine());
                    int dragon_ap = Integer.parseInt(br.readLine());
                    int dragon_cc = Integer.parseInt(br.readLine());
                    int dragon_acc = Integer.parseInt(br.readLine());
                    int dragon_damage = Integer.parseInt(br.readLine());
                    int dragon_full_hp = Integer.parseInt(br.readLine());
                    dragon.setLevel(dragon_level);
                    dragon.setHealthpoint(dragon_hp);
                    dragon.setAttackpoint(dragon_ap);
                    dragon.setCriticalChance(dragon_cc);
                    dragon.setAccuracy(dragon_acc);
                    dragon.setDg_damage(dragon_damage);
                    dragon.setFull_hp(dragon_full_hp);
                    
                    difficulty = Integer.parseInt(br.readLine());
                
                    br.close();
                    
                    System.out.println("\n[LOADING... ...]\n");
                
                    //delete decrypt file
                    decryptedata.delete();
                
                } catch (IOException e) {
                    System.out.println("\nNo game is saved.");
                    System.out.println("Please create a new game.");
                }
                
                if(difficulty==1) {
                    System.out.println("Welcome to Till The End (NORMAL)");
                    normal.gameMethod(season,tax,tower,wall,citizen,dragon);
                }
                else if(difficulty==2) {
                    System.out.println("Welcome to Till The End (HARD)");
                    hard.gameMethod(season,tax,tower,wall,citizen,dragon);
                }
                break;
                
            case 2:
                /* NEW GAME */
                System.out.println("\n\nPlease select a difficulty level: ");
                System.out.println("\n1. NORMAL");
                System.out.println("2. HARD\n");
                System.out.print("Please enter your command: ");
                int input_new = in.nextInt();
                while( !(input_new==1 || input_new==2)){
                    System.out.println("You enter an invalid command.");
                    System.out.print("Please enter you command: ");
                    input_new = in.nextInt();
                }
                System.out.println("\n.................................................");
                tax.setCurrent_gold(200);                       //set initial gold
                
                switch(input_new){
                    case 1:
                        /* NORMAL */
                        System.out.println(normal.loading_page());      //loading page
                        normal.gameMethod(season,tax,tower,wall,citizen,dragon);
                        break;
                        
                    case 2:
                        /* HARD */
                        System.out.println(hard.loading_page());      //loading page
                        hard.gameMethod(season,tax,tower,wall,citizen,dragon);
                        break;
                }
        }
    }
    
    public static void encryptDecrypt(String key, int cipherMode, File in, File out) 
    throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException{
        
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        
        DESKeySpec DESks = new DESKeySpec(key.getBytes());
        
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey sk = skf.generateSecret(DESks);
        
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        
        if(cipherMode == Cipher.ENCRYPT_MODE){
            cipher.init(Cipher.ENCRYPT_MODE, sk, SecureRandom.getInstance("SHA1PRNG"));
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            write(cis, fos);
        }
        else if(cipherMode == Cipher.DECRYPT_MODE){
            cipher.init(Cipher.DECRYPT_MODE, sk, SecureRandom.getInstance("SHA1PRNG"));
            CipherOutputStream cos = new CipherOutputStream(fos,cipher);
            write(fis,cos);
        }
    }
    
    private static void write(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[64];
        int numOfBytesRead;
        
        while((numOfBytesRead = in.read(buffer)) != -1){
            out.write(buffer, 0, numOfBytesRead);
        }
        out.close();
        in.close();
    }
    
}
