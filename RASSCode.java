import java.io.*;
class RASS
{
    static void main()throws IOException
    {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in)); //For console input
        String filename;
        System.out.println("Enter the path to the file: ");
        filename=br.readLine(); //file path stored

        FileReader fr=new FileReader(filename); //to access file
        BufferedReader read=new BufferedReader(fr); //to read file
        String line="";     //stores each line of file
        String contents=""; //stores entire plaintext
        while(true)
        {
            line=read.readLine();   

            if(line==null)
                break;    //EOF

            contents+="\n"+line;    //add to plaintext
        }

        GenerateKey k1=new GenerateKey();
        String key1=k1.gen(16);     //generate 16 bit key
        String key2=k1.gen(16);     //generate 16 bit key
        String cipher="";
        while(true)                 //menu-handling
        {
            System.out.println("1. Encrypt file.\n2. Decrypt encrypted version of file\n0. Exit");
            int ch=Integer.parseInt(br.readLine());
            switch(ch)
            {
                case 1: Encryption ob=new Encryption();
                        cipher=ob.enc(contents,key1,key2);  //function call
                        break;
                        
                case 2: Decryption obj=new Decryption();
                        obj.dec(cipher,key1,key2);          //function call
                        break;
                        
                case 0: System.exit(0);
                default: System.out.println("Invalid Input");
            }
        }
    }
}


class GenerateKey 
{
    //PseudoRandom Function
    public String gen(int len)
    {
        String key="";
        for(int i=0;i<len;i++)
        {
            key+=(Math.random()>0.5)?1:0;   //0 or 1 stored
        }
        return key;
    }
}

class Encryption
{
    public static String enc(String plaintext, String key1, String key2)
    {
        Methods obj=new Methods();
        System.out.println("Plaintext: "+plaintext);    //print plaintext

        long start=System.nanoTime();           //current system time

        int length=plaintext.length();
        String firsthalf,secondhalf;

        //partitioning

        if(length%2==0)
        {
            firsthalf=plaintext.substring(0,length/2);
            secondhalf=plaintext.substring(length/2);
        }
        else
        {
            firsthalf=plaintext.substring(0,(length+1)/2);
            secondhalf=plaintext.substring((length+ 1)/2);
        }

        String ciphertext="";                   //Holds final ciphertext

        for(int i=0;i<firsthalf.length();i++)   //block by block traversal
        {
            if(i%2==0)
                ciphertext+=obj.EvenMode(firsthalf.charAt(i),key1); //even=straight
            else
                ciphertext+=obj.OddMode(firsthalf.charAt(i),key1);  //odd=criss-cross
        }
        for(int i=0;i<secondhalf.length();i++)
        {
            if(i%2==0)
                ciphertext+=obj.EvenMode(secondhalf.charAt(i),key2); //even=straight
            else
                ciphertext+=obj.OddMode(secondhalf.charAt(i),key2); //odd=criss-cross
        }
        System.out.println("\nEncrypted: "+ciphertext); //print result
        long end=System.nanoTime(); //system time

        System.out.println("\nTime for encryption: "+(double)(end-start)/1000000000+" seconds");
        return ciphertext;
    }
}

class Decryption
{
    public static void dec(String ciphertext, String key1, String key2)
    {
        //decryption below
        Methods obj=new Methods();
        long start,end;
        int length=ciphertext.length();
        String firsthalf,secondhalf;

        start=System.nanoTime();    //current time
        String decrypted="";

        //partitioning

        if(length%2==0)
        {
            firsthalf=ciphertext.substring(0,length/2);
            secondhalf=ciphertext.substring(length/2);
        }
        else
        {
            firsthalf=ciphertext.substring(0,(length+1)/2);
            secondhalf=ciphertext.substring((length+ 1)/2);
        }

        for(int i=0;i<firsthalf.length();i++)   //block by block traversal
        {
            if(i%2==0)
                decrypted+=obj.EvenMode(firsthalf.charAt(i),key1);  //Even=straight
            else
                decrypted+=obj.OddMode(firsthalf.charAt(i),key1);   //odd=criss-cross
        }
        for(int i=0;i<secondhalf.length();i++)  //block by block traversal
        {
            if(i%2==0)
                decrypted+=obj.EvenMode(secondhalf.charAt(i),key2); //even=straight
            else
                decrypted+=obj.OddMode(secondhalf.charAt(i),key2);  //odd=criss-cross
        }
        end=System.nanoTime();
        System.out.println("\n\nDecrypted: "+decrypted);
        System.out.println("\nTime for decryption: "+(double)(end-start)/1000000000+" seconds");
    }
}

class Methods
{    
    //straight
    static char EvenMode(int x, String key) 
    {
        int i;
        String str="";
        StringBuffer sb=new StringBuffer(str);

        String a=String.format("%16s",Integer.toBinaryString(x));   //convert to binary string
        String aa="";
        String kk="";

        //for 16 bit padding
        for(i=0;i<a.length();i++)
        {
            if(a.charAt(i)==' ')
                aa+='0';
            else
                aa+=a.charAt(i);
        }

        for(i=0;i<key.length();i++)
        {
            if(key.charAt(i)==' ')
                kk+='0';
            else
                kk+=key.charAt(i);
        }

        //XOR operation
        for(i=0;i<=15;i++)
        {
            sb.append((Integer.parseInt(""+aa.charAt(i))^Integer.parseInt(""+kk.charAt(i))));
        }
        String ss=sb.toString();
        return (char)Integer.parseInt(ss,2);    //returns decimal form of binary String
    }

    //criss-cross
    static char OddMode(int x,String key)
    {
        int i;
        String str="";
        StringBuffer sb=new StringBuffer(str);

        String a=String.format("%16s",Integer.toBinaryString(x));   //binary string
        String aa="";
        String kk="";

        //16 bit padding
        for(i=0;i<a.length();i++)
        {
            if(a.charAt(i)==' ')
                aa+='0';
            else
                aa+=a.charAt(i);
        }

        for(i=0;i<key.length();i++)
        {
            if(key.charAt(i)==' ')
                kk+='0';
            else
                kk+=key.charAt(i);
        }

        //traversal
        for(i=0;i<=15;i++)
        {
            //first 8 bits of text with last 8 bits of key
            if(i<8)
                sb.append((Integer.parseInt(""+aa.charAt(i))^Integer.parseInt(""+kk.charAt(i+8))));
            //next 8 bits of text with first 8 bits of key
            else
                sb.append((Integer.parseInt(""+aa.charAt(i))^Integer.parseInt(""+kk.charAt(i-8))));
        }
        String ss=sb.toString();
        return (char)Integer.parseInt(ss,2);    //returns decimal form of binary string
    }

}
