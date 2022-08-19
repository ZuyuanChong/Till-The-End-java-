package TillTheEnd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

public class Normal {
    Scanner in = new Scanner(System.in);
    
    int input = 0;
    
    public void gameMethod(Season season, Tax tax, Tower tower, Wall wall, Citizen citizen, Dragon dragon){
        /* GAME START */
        while(dragon.getGame_condition()){
            /* DRAGON'S ATTACK */
            try{
                FileInputStream fileInputStream = new FileInputStream("dragon_attack.mp3"); 
                Player player = new Player(fileInputStream);
                player.play();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(JavaLayerException e){
                e.printStackTrace();
            }
            System.out.println(dragon.toString(season,tax));
            in.nextLine();
            dragon.dragon_round(tower, wall);              
            dragon.dragon_round_end(tax, input);
            
            //break the while loop if the player loses
            if(!dragon.getGame_condition()) { break; }
            
            //return the Tower's accuracy back to 80% during "Rainy" or "Hunger"
            if( (season.getEvent()=="Rainy" || season.getEvent()=="Hunger")){
                tower.setAccuracy(tower.getAccuracy()+20);
            }
            
            /* TAXES */
            tax.generateTax();
            tax.receiveTax();
            
            /* UPDATE SEASON AND YEAR */
            season.updateTime();
            
            /* EVENT SELECTOR for each season */
            season.event_selector(tax);
            
            /* INTERFACE */
            do{
                // print commands
                System.out.println(toString(season,tax));
                input_command();
                
                switch(input){
                    case 1:
                        /* TOWER */
                        System.out.println("----------------------------TOWER----------------------------");
                        System.out.println(tower.toString(season,tax));
                        tower.input_tower();
                        if(tower.getInput_tower()==4){
                            System.out.println("-------------------------------------------------------------");
                            break;
                        }
                        tower.tower_choice(tax);
                        System.out.println("-------------------------------------------------------------");
                        break;
                        
                    case 2:
                        /* WALL */
                        System.out.println("----------------------------WALL----------------------------");
                        System.out.println(wall.toString(season,tax));
                        wall.input_wall();
                        if(wall.getInput_wall()==3){
                            System.out.println("------------------------------------------------------------");
                            break;
                        }
                        wall.wall_choice(tax);
                        System.out.println("------------------------------------------------------------");
                        break;
                        
                    case 3:
                        /* CITIZEN */
                        System.out.println("----------------------------CITIZEN----------------------------");
                        System.out.println(citizen.toString(season,tax));
                        citizen.input_citizen();
                        if(citizen.getInput_citizen()==7){
                            System.out.println("---------------------------------------------------------------");
                            break;
                        } 
                        citizen.citizen_choice(tax);
                        System.out.println("---------------------------------------------------------------");
                        break;
                        
                    case 4:
                        /* DRAGON (I am all ready!) */
                        System.out.println("\n[SAVING... ...]\n");
                        System.out.println("----------------------------DRAGON'S ATTACK----------------------------");
                        break;
                }
                
            } while(input!=4);
            
            //save module
            SaveIntoFile(season, tax, tower, wall, citizen, dragon);
            
            //File Encryption
            File data = new File("SaveLoadTTE.txt");
            File encryptedata = new File("encrypt.txt");
            
            try{
                encryptDecrypt("123456789",Cipher.ENCRYPT_MODE, data, encryptedata);
            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IOException e){
                e.printStackTrace();
            }
            
            //delete raw file
            data.delete();
            
            season.nextSesaon();
        }
        
        
    }
    
    public String toString(Season season, Tax tax){
        return ("Event : "+season.getEvent()+
                "\nTax received from citizens this season : "+tax.getGold()+
                "\nYear : "+season.getYear()+
                "\nSeason : "+season.getSeason()+
                "\nGold : "+tax.getCurrent_gold()+
                "\n1. Tower"+
                "\n2. Wall"+
                "\n3. Citizens"+
                "\n4. I am all ready!"
                );
    }
    
    public String loading_page(){
        return ("\nYou have selected NORMAL mode.\n"+
                "Creating new game..."+
                "\n... ...... ...\n\n"+
                "Welcome to Till The End (NORMAL)"
                );
    }
    
    public void input_command(){
        System.out.print("Please enter your command: ");
        input = new java.util.Scanner(System.in).nextInt();
        while(input>4){
            System.out.println("You enter an invalid command.");
            System.out.print("Please enter you command:");
            input = new java.util.Scanner(System.in).nextInt();
        }
        System.out.println("\n");
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
    
    public static void SaveIntoFile(Season season, Tax tax, Tower tower, Wall wall, Citizen citizen, Dragon dragon){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter("SaveLoadTTE.txt"));
            
            //Season - time
            bw.write("" + season.getTime());
            bw.newLine();
            
            //Tax - current_gold
            bw.write(""+tax.getCurrent_gold());
            bw.newLine();
            
            //Tower - attackpoint, critical chance, accuracy
            bw.write(""+tower.getAttackpoint());
            bw.newLine();
            bw.write(""+tower.getCriticalChance());
            bw.newLine();
            bw.write(""+tower.getAccuracy());
            bw.newLine();
            
            //Wall - healthpoint, block percentage
            bw.write(""+wall.getHealthpoint());
            bw.newLine();
            bw.write(""+wall.getBlockPercentage());
            bw.newLine();
            
            //Citizen - emotional, nervous, lazy, berserk, diligent, fearless
            bw.write(""+citizen.getEmotional());
            bw.newLine();
            bw.write(""+citizen.getNervous());
            bw.newLine();
            bw.write(""+citizen.getLazy());
            bw.newLine();
            bw.write(""+citizen.getBerserk());
            bw.newLine();
            bw.write(""+citizen.getDiligent());
            bw.newLine();
            bw.write(""+citizen.getFearless());
            bw.newLine();
            
            //Dragon - level, healthpoint, attaackpoint, critical chance, accuracy, damage, full healthpoint
            bw.write(""+dragon.getLevel());
            bw.newLine();
            bw.write(""+dragon.getHealthpoint());
            bw.newLine();
            bw.write(""+dragon.getAttackpoint());
            bw.newLine();
            bw.write(""+dragon.getCriticalChance());
            bw.newLine();
            bw.write(""+dragon.getAccuracy());
            bw.newLine();
            bw.write(""+dragon.getDg_damage());
            bw.newLine();
            bw.write(""+dragon.getFull_hp());
            bw.newLine();
            
            //difficult of game - 1 for NORMAL 
            bw.write(""+1);
            bw.newLine();
            
            bw.close();
        } catch (IOException e){
            System.out.println("File output error");
        }
    }
            
    
}
