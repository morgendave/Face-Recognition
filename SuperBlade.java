import java.util.*;
import java.io.*;
//name: Zhiwei Zhao & Haolun Yan; ID: #999486366 & #999472053
public class SuperBlade {
	public static float[] init(String img) {
		final int Width = 128;
        final int Height = 120;
		try
		{
			float[] intensities = new float[Width * Height];
			int index = 0;

            BufferedReader in = new BufferedReader(new FileReader( img ));
			String str;
            while((str = in.readLine()) != null)
            {
                final StringTokenizer parser = new StringTokenizer(str);
                while ( parser.hasMoreTokens( ) )
                {
                    final int val = new Integer( parser.nextToken( ) );
                    intensities[index] = val;
					index++;
                }
            }
            in.close();
			return intensities;
		}
		catch(Exception ex)
		{
			ex.printStackTrace( ) ;
            throw new RuntimeException("Unable to parse file");
		}
	}

	public static void test_recall(NeuralLearning nn, float[] inputs, String imgName) {
        float[] results = nn.recall(inputs);
        System.out.print("case: " + imgName);
        System.out.print(" results: ");
        for (int i = 0; i < results.length; i++) {
		if (results[i] > 0.5) System.out.print(results[i] + " Male " + (results[i]-0.5)/0.5);
		else System.out.print(results[i] + " Female " + (0.5-results[i])/0.5);
        System.out.println();
		}
    }

	public static float[] converter(float[] input)
	{
		float [] temp = new float[input.length];
		for (int i = 0; i < input.length; i++) {
			temp[i] = input[i] / 255;
		}
		return temp;
	}
	
	public static void outputTXT(int mysize_of_input, NeuralLearning myneural, ArrayList<String> myimgList){
        int size_of_input = mysize_of_input;
        NeuralLearning neural = myneural;
        ArrayList<String> imgList = myimgList;
        float[] input = new float[imgList.size()];
        float[] output = new float[1];
        float maxW11 = -1000.0f;
        float maxW12 = -1000.0f;
        float maxW13 = -1000.0f;
        float minW11 = 1000.0f;
        float minW12 = 1000.0f;
        float minW13 = 1000.0f;
        for(String img : imgList)
        {
            input = init(img);
            char firstChar = img.charAt(0);
            if (firstChar == 'F') {
                output[0] = 0.0f;
            } 
            else if (firstChar == 'M') {
                output[0] = 1.0f;
            } 
            else
                System.out.println("error");
            
            neural.addTrainingExample(converter(input), output);
        }
        double error = 0;
        for (int p = 0; p < 500; p++) {
            error += neural.backPropLearning();
        }
        neural.save("test.neural");
        
        for (int d = 0; d < size_of_input; d++) {
            for (int e = 0; e < 3; e++) {
                if(e == 0){
                    if (maxW11 < neural.W1[d][e]){
                        maxW11 = neural.W1[d][e];
                    }
                    if (minW11 > neural.W1[d][e]){
                        minW11 = neural.W1[d][e];
                    }
                }
                else if(e == 1){
                    if (maxW12 < neural.W1[d][e]){
                        maxW12 = neural.W1[d][e];
                    }
                    if (minW12 > neural.W1[d][e]){
                        minW12 = neural.W1[d][e];
                    }
                }
                else if(e == 2){
                    if (maxW13 < neural.W1[d][e]){
                        maxW13 = neural.W1[d][e];
                    }
                    if (minW13 > neural.W1[d][e]){
                        minW13 = neural.W1[d][e];
                    }
                }
            }
        }
        
        
        try {
            PrintWriter out0 = new PrintWriter(new FileWriter("out0.txt"));
            PrintWriter out1 = new PrintWriter(new FileWriter("out1.txt"));
            PrintWriter out2 = new PrintWriter(new FileWriter("out2.txt"));
            int[] final0 = new int[120*128];
            int[] final1 = new int[120*128];
            int[] final2 = new int[120*128];
            
            for(int y = 0; y < 120*128; y++){
                out0.printf("%d ", (int)Math.floor(((neural.W1[y][0] - minW11) / (maxW11-minW11))*255));
                out1.printf("%d ", (int)Math.floor(((neural.W1[y][1] - minW12) / (maxW12-minW12))*255));
                out2.printf("%d ", (int)Math.floor(((neural.W1[y][2] - minW13) / (maxW13-minW13))*255));
            }
            
            out0.close();
            out1.close();
            out2.close();
        } 
        catch (IOException e){
            e.printStackTrace();
        }
        
        System.out.println("Hidden nodes to Output nodes");
        for (int d = 0; d < 3; d++) {
            for (int e = 0; e < 1; e++) {
                System.out.print(neural.W2[d][e] + " ");
            }
            System.out.println();
        }

    }

	public static void main(String[] args)
	{		
		final int size_of_input = 128*120;
		NeuralLearning neural = new NeuralLearning(size_of_input, 3, 1);
		ArrayList< String > imgList = new ArrayList< String >();
		boolean isTest;
		
		if (args[0].equals("-train"))
		{
			isTest = false;
		}
		else if (args[0].equals("-test"))
		{
			isTest = true;
		}
		else if(args[0].equals("-part2"))
		{
			randomFoldTest();
			return;
		}
		else
		{
			System.out.println("Please use options: -train or -test. Example: java SuperBlade [option] path/to/file");
			return;
		}

        // Parse through the command line arguments
        int k = 1;
        try
        {
            while(k < args.length)
            {
                imgList.add( args[k] );
                k--;
                k += 2;
            }
        }
        catch(IndexOutOfBoundsException ioob)
        {
            System.err.println("Invalid Arguments: " + args[k]);
            System.exit(2);
        }
        catch(Exception e)
        {
            System.err.println("Unknown Error");
            System.exit(4);
        }
		
		float[] input;
		if (isTest == false) {
			outputTXT(size_of_input, neural, imgList);
		}
		
		NeuralLearning nn2 = NeuralLearning.Factory("test.neural");
		for (String img : imgList) {
			input = init(img);
			test_recall(nn2, converter(input), img);
		}
	}
	
	static String[] testlist = {
	"10_1_1.txt","12_5_3.txt","15_3_4.txt","18_3_2.txt","20_2_4.txt","4_2_1.txt","7_1_2.txt",
	"10_1_2.txt","12_5_4.txt","15_5_1.txt","18_3_3.txt","20_2_5.txt","4_2_3.txt","7_1_3.txt",
	"10_1_3.txt","13_1_1.txt","15_5_3.txt","18_3_4.txt","20_3_2.txt","4_2_4.txt","7_1_4.txt",
	"10_1_4.txt","13_1_2.txt","15_5_4.txt","18_5_1.txt","20_3_4.txt","4_3_1.txt","7_2_1.txt",
	"10_2_1.txt","13_1_3.txt","16_1_1.txt","18_5_3.txt","20_5_1.txt","4_3_2.txt","7_2_3.txt",
	"10_2_3.txt","13_1_4.txt","16_1_2.txt","18_5_4.txt","20_5_3.txt","4_3_3.txt","7_2_4.txt",
	"10_2_4.txt","13_2_1.txt","16_1_3.txt","19_1_1.txt","20_5_4.txt","4_3_4.txt","7_3_1.txt",
	"10_3_1.txt","13_2_3.txt","16_2_1.txt","19_1_2.txt","2_1_1.txt","4_5_1.txt","7_3_2.txt",
	"10_3_2.txt","13_2_4.txt","16_2_3.txt","19_1_3.txt","2_1_2.txt","4_5_3.txt","7_3_3.txt",
	"10_3_3.txt","13_3_1.txt","16_2_4.txt","19_1_4.txt","2_1_3.txt","4_5_4.txt","7_3_4.txt",
	"10_3_4.txt","13_3_2.txt","16_3_1.txt","19_2_1.txt","2_1_4.txt","5_1_1.txt","7_5_1.txt",
	"10_5_1.txt","13_3_3.txt","16_3_2.txt","19_2_3.txt","2_2_1.txt","5_1_2.txt","7_5_3.txt",
	"10_5_3.txt","13_3_4.txt","16_3_3.txt","19_2_4.txt","2_2_3.txt","5_1_3.txt","7_5_4.txt",
	"10_5_4.txt","13_5_1.txt","16_3_4.txt","19_3_1.txt","2_2_4.txt","5_1_4.txt","8_1_1.txt",
	"11_1_1.txt","13_5_3.txt","16_5_1.txt","19_3_2.txt","2_3_1.txt","5_2_1.txt","8_1_2.txt",
	"11_1_2.txt","13_5_4.txt","16_5_3.txt","19_3_3.txt","2_3_2.txt","5_2_3.txt","8_1_3.txt",
	"11_1_3.txt","14_1_1.txt","16_5_4.txt","19_3_4.txt","2_3_3.txt","5_2_4.txt","8_1_4.txt",
	"11_1_4.txt","14_1_2.txt","17_1_1.txt","19_5_1.txt","2_3_4.txt","5_3_1.txt","8_2_1.txt",
	"11_2_3.txt","14_1_3.txt","17_1_2.txt","19_5_3.txt","2_5_1.txt","5_3_2.txt","8_2_3.txt",
	"11_2_4.txt","14_1_4.txt","17_1_3.txt","19_5_4.txt","2_5_3.txt","5_3_3.txt","8_2_4.txt",
	"11_3_1.txt","14_2_1.txt","17_1_4.txt","1_1_1.txt","2_5_4.txt","5_3_4.txt","8_3_1.txt",
	"11_3_2.txt","14_2_3.txt","17_2_1.txt","1_1_2.txt","3_1_1.txt","5_5_1.txt","8_3_2.txt",
	"11_3_3.txt","14_2_4.txt","17_2_3.txt","1_1_3.txt","3_1_2.txt","5_5_3.txt","8_3_3.txt",
	"11_3_4.txt","14_3_1.txt","17_2_4.txt","1_2_1.txt","3_1_3.txt","5_5_4.txt","8_3_4.txt",
	"11_5_1.txt","14_3_2.txt","17_3_1.txt","1_2_3.txt","3_1_4.txt","6_1_1.txt","8_5_1.txt",
	"11_5_3.txt","14_3_3.txt","17_3_2.txt","1_2_4.txt","3_2_1.txt","6_1_2.txt","8_5_3.txt",
	"11_5_4.txt","14_3_4.txt","17_3_3.txt","1_3_1.txt","3_2_3.txt","6_1_3.txt","8_5_4.txt",
	"12_1_1.txt","14_5_1.txt","17_3_4.txt","1_3_2.txt","3_2_4.txt","6_1_4.txt","9_1_1.txt",
	"12_1_2.txt","14_5_3.txt","17_5_1.txt","1_3_3.txt","3_3_1.txt","6_2_1.txt","9_1_2.txt",
	"12_1_3.txt","15_1_1.txt","17_5_3.txt","1_3_4.txt","3_3_2.txt","6_2_3.txt","9_1_3.txt",
	"12_1_4.txt","15_1_2.txt","17_5_4.txt","1_5_1.txt","3_3_3.txt","6_2_4.txt","9_1_4.txt",
	"12_2_1.txt","15_1_3.txt","18_1_1.txt","1_5_3.txt","3_3_4.txt","6_3_1.txt","9_2_1.txt",
	"12_2_3.txt","15_1_4.txt","18_1_2.txt","1_5_4.txt","3_5_1.txt","6_3_2.txt","9_2_3.txt",
	"12_2_4.txt","15_2_1.txt","18_1_3.txt","20_1_1.txt","3_5_3.txt","6_3_3.txt","9_2_4.txt",
	"12_3_1.txt","15_2_3.txt","18_1_4.txt","20_1_2.txt","3_5_4.txt","6_3_4.txt","9_3_1.txt",
	"12_3_2.txt","15_2_4.txt","18_2_1.txt","20_1_3.txt","4_1_1.txt","6_5_1.txt","9_3_2.txt",
	"12_3_3.txt","15_3_1.txt","18_2_3.txt","20_1_4.txt","4_1_2.txt","6_5_3.txt","9_3_4.txt",
	"12_3_4.txt","15_3_2.txt","18_2_4.txt","20_2_1.txt","4_1_3.txt","6_5_4.txt","9_5_1.txt",
	"12_5_1.txt","15_3_3.txt","18_3_1.txt","20_2_3.txt","4_1_4.txt","7_1_1.txt","9_5_3.txt"};
	
	static String[] femaleFiles = {
	"16_1_1.txt","16_3_3.txt","17_1_4.txt","17_5_1.txt","18_2_3.txt","18_5_4.txt","5_3_1.txt",
	"16_1_2.txt","16_3_4.txt","17_2_1.txt","17_5_3.txt","18_2_4.txt","5_1_1.txt","5_3_2.txt",
	"16_1_3.txt","16_5_1.txt","17_2_3.txt","17_5_4.txt","18_3_1.txt","5_1_2.txt","5_3_3.txt",
	"16_2_1.txt","16_5_3.txt","17_2_4.txt","18_1_1.txt","18_3_2.txt","5_1_3.txt","5_3_4.txt",
	"16_2_3.txt","16_5_4.txt","17_3_1.txt","18_1_2.txt","18_3_3.txt","5_1_4.txt","5_5_1.txt",
	"16_2_4.txt","17_1_1.txt","17_3_2.txt","18_1_3.txt","18_3_4.txt","5_2_1.txt","5_5_3.txt",
	"16_3_1.txt","17_1_2.txt","17_3_3.txt","18_1_4.txt","18_5_1.txt","5_2_3.txt","5_5_4.txt",
	"16_3_2.txt","17_1_3.txt","17_3_4.txt","18_2_1.txt","18_5_3.txt","5_2_4.txt"};

	static ArrayList<String> fold1 = new ArrayList<String>();
	static ArrayList<String> fold2 = new ArrayList<String>();
	static ArrayList<String> fold3 = new ArrayList<String>();
	static ArrayList<String> fold4 = new ArrayList<String>();
	static ArrayList<String> fold5 = new ArrayList<String>();
	static ArrayList<String> usedFold = new ArrayList<String>();
	static ArrayList<String> testFold = new ArrayList<String>();
	static ArrayList<String> checkFemalePath = new ArrayList<String> ();
	
	public static void randomFoldTest() {
		ArrayList< Integer > folds = new ArrayList< Integer > ();
		ArrayList< String > temp = new ArrayList< String > ();
		String pathToTestFolder = new String("RandomList/");
		
		for (int j = 0; j < testlist.length; j++) {
			temp.add(new String(testlist[j]));
		}
		int numOfFold = (int)(testlist.length / 5);
		
		for (int i = 0; i < testlist.length; i++) {
			Integer index = new Integer((int)(Math.random() * temp.size()));
			if (i < numOfFold) {
				fold1.add(pathToTestFolder + temp.get(index));
			} else if (i < 2 * numOfFold) {
				fold2.add(pathToTestFolder + temp.get(index));
			} else if (i < 3 * numOfFold) {
				fold3.add(pathToTestFolder + temp.get(index));
			} else if (i < 4 * numOfFold) {
				fold4.add(pathToTestFolder + temp.get(index));
			} else if (i < 5 * numOfFold) {
				fold5.add(pathToTestFolder + temp.get(index));
			}
			temp.remove(index.intValue());
		}
		
		for (int dd = 0; dd < femaleFiles.length; dd++) {
			checkFemalePath.add(new String(pathToTestFolder + femaleFiles[dd]));
		}
		float[] meanList = new float[5];
		
		useFolds(fold2, fold3, fold4, fold5);
		meanList[0] = computedMean(fold1, testFold);
		testFold.clear();
		
		useFolds(fold3, fold4, fold5, fold1);
		meanList[1] = computedMean(fold2, testFold);
		testFold.clear();
		
		useFolds(fold4, fold5, fold1, fold2);
		meanList[2] = computedMean(fold3, testFold);
		testFold.clear();
		
		useFolds(fold5, fold1, fold2, fold3);
		meanList[3] = computedMean(fold4, testFold);
		testFold.clear();
		
		useFolds(fold1, fold2, fold3, fold4);
		meanList[4] = computedMean(fold5, testFold);
		testFold.clear();
		
		float totalMean = 0;
		for (int d = 0; d < 5; d++) {
			totalMean += meanList[d];
			System.out.println("Mean of Test " + d + " is " + meanList[d]);
		}
		float meanOfTest = totalMean / 5;
		float sumDiff = 0;
		System.out.println("Mean of All test is " + meanOfTest);
		
		for (int k = 0; k < 5; k++) {
			sumDiff += Math.pow((meanList[k] - meanOfTest), 2);
		}
		double standStd = Math.sqrt(sumDiff / 4.0f);
		System.out.println("STDDEV is " + standStd);
	}
	
	public static void useFolds(ArrayList<String> f1, ArrayList<String> f2, ArrayList<String> f3, ArrayList<String> f4) {
		for (int i = 0; i < f1.size(); i++) {
			testFold.add(f1.get(i));
		}
		for (int i = 0; i < f2.size(); i++) {
			testFold.add(f2.get(i));
		}
		for (int i = 0; i < f3.size(); i++) {
			testFold.add(f3.get(i));
		}
		for (int i = 0; i < f4.size(); i++) {
			testFold.add(f4.get(i));
		}
	}
	
	public static float computedMean(ArrayList<String> foldForTest, ArrayList<String> train)
	{
		final int size_of_input = 128*120;
		NeuralLearning neural = new NeuralLearning(size_of_input, 3, 1);
		
		float[] input;
		float[] output = new float[1];
		for(String img : train)
		{
			input = init(img);
			if (checkFemalePath.contains(img)) {
				output[0] = 0.0f;
			} else {
				output[0] = 1.0f;
			}
			
			neural.addTrainingExample(converter(input), output);
		}
		
		for (int k = 0; k < 500; k++) {
			neural.backPropLearning();
		}
		
		int numOfRight = 0;
		for (String img : foldForTest) {
			input = init(img);
			numOfRight = part2Result(neural, converter(input), img, numOfRight);
		}
		System.out.println("the number of rightness: " + numOfRight + " and Mean: " + ((float)numOfRight) / foldForTest.size());
		
		return ((float)numOfRight) / foldForTest.size();
	}
	
	public static int part2Result(NeuralLearning nn, float[] inputs, String imgName, int numOfRight) {
		float[] results = nn.recall(inputs);
		System.out.print("case: " + imgName);
        System.out.print(" results: ");
		for (int i = 0; i < results.length; i++) System.out.print(results[i] + " ");
        System.out.println();

		for (int i = 0; i < results.length; i++)
		{
			if (results[i] > 0.5 && !checkFemalePath.contains(imgName)) {
				numOfRight++;
			} else if (results[i] <= 0.5 && checkFemalePath.contains(imgName)) {
				numOfRight++;
			}
		}
		System.out.println();
		return numOfRight;
	}
}

class NeuralLearning implements Serializable{
	protected int numInputs;
    protected int numHidden;
    protected int numOutputs;

    protected int numTraining;

    public float inputs[];
    protected float hidden[];
    public float outputs[];

    protected float W1[][];
    protected float W2[][];

    protected float output_errors[];
    protected float hidden_errors[];

    public float learningRate = 0.1f;

    transient protected Vector<float[]> inputTraining = new Vector<float[]>();
    transient protected Vector<float[]> outputTraining = new Vector<float[]>();

	NeuralLearning(int num_in, int num_hidden, int num_output) {
		numInputs = num_in;
        numHidden = num_hidden;
        numOutputs = num_output;
        inputs = new float[numInputs];
        hidden = new float[numHidden];
        outputs = new float[numOutputs];
        W1 = new float[numInputs][numHidden];
        W2 = new float[numHidden][numOutputs];
        randomizeWeights();

        output_errors = new float[numOutputs];
        hidden_errors = new float[numHidden];
	}
	
	public void addTrainingExample(float[] inputs, float[] outputs) {
        if (inputs.length != numInputs || outputs.length != numOutputs) {
            System.out.println("addTrainingExample(): array size is wrong");
            return;
        }
        float[] in = new float[inputs.length];
        float[] out = new float[outputs.length];
        for(int i = 0; i < inputs.length; i++) 
		{
        	in[i] = inputs[i];
		}
        for(int j = 0; j < outputs.length; j++)
		{
        	out[j] = outputs[j];
		}
        inputTraining.addElement(in);
        outputTraining.addElement(out);
    }

	public static NeuralLearning Factory(String serialized_file_name) {
        NeuralLearning nn = null;
        try {
            InputStream ins = ClassLoader.getSystemResourceAsStream(serialized_file_name);
            if (ins == null) {
                System.out.println("CachedExamples(): failed to open 'cache.dat'");
                System.exit(1);
            } else {
                ObjectInputStream p = new ObjectInputStream(ins);
                nn = (NeuralLearning) p.readObject();
                nn.inputTraining = new Vector<float[]>();
                nn.outputTraining = new Vector<float[]>();
                ins.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return nn;
    }

    public void save(String file_name) {
        try {
            FileOutputStream ostream = new FileOutputStream(file_name);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(this);
            p.flush();
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float[] recall(float[] in) {
        for (int i = 0; i < numInputs; i++) inputs[i] = in[i];
        forwardPass();
        float[] ret = new float[numOutputs];
        for (int i = 0; i < numOutputs; i++)
			ret[i] = outputs[i];
		
        return ret;
    }
    
    public void randomizeWeights() {
        // Randomize weights here:
        for (int ii = 0; ii < numInputs; ii++)
            for (int hh = 0; hh < numHidden; hh++)
                W1[ii][hh] =
                        (float) Math.random() / 5.0f - 0.1f;
        for (int hh = 0; hh < numHidden; hh++)
            for (int oo = 0; oo < numOutputs; oo++)
                W2[hh][oo] =
                        (float) Math.random()  / 5.0f - 0.1f;
    }

    public void forwardPass() {
        int i, h, o;
        for (h = 0; h < numHidden; h++) {
            hidden[h] = 0.0f;
        }
        for (i = 0; i < numInputs; i++) {
            for (h = 0; h < numHidden; h++) {
                hidden[h] +=
                        inputs[i] * W1[i][h];
            }
        }
		
		for (int d = 0; d < numHidden; d++) {
			hidden[d] /= 1536;
		}
		
        for (o = 0; o < numOutputs; o++)
            outputs[o] = 0.0f;
        for (h = 0; h < numHidden; h++) {
            for (o = 0; o < numOutputs; o++) {
                outputs[o] +=
                        sigmoid(hidden[h]) * W2[h][o];
            }
        }
        for (o = 0; o < numOutputs; o++) {
            outputs[o] = sigmoid(outputs[o]);
		}
    }
    
	public float backPropLearning() {
        return backPropLearning(inputTraining, outputTraining);
    }

	private int current_example = 0;

    public float backPropLearning(Vector v_ins, Vector v_outs) {
      int i, h, o;
      float error = 0.0f;
      int num_cases = v_ins.size();
      for (int example=0; example<num_cases; example++) {
        // zero out error arrays:
        for (h=0; h<numHidden; h++)
           hidden_errors[h] = 0.0f;
        for (o=0; o<numOutputs; o++)
           output_errors[o] = 0.0f;
        // copy the input values:
        for (i=0; i<numInputs; i++) {
          inputs[i] = ((float [])v_ins.elementAt(current_example))[i];
        }
        // copy the output values:
        float [] outs = (float [])v_outs.elementAt(current_example);

        // perform a forward pass through the network:

        forwardPass();

        for (o=0; o<numOutputs; o++)  {
            output_errors[o] = (outs[o] - outputs[o]) * sigmoidP(outputs[o]);
        }
        for (h=0; h<numHidden; h++) {
          hidden_errors[h] = 0.0f;
          for (o=0; o<numOutputs; o++) {
             hidden_errors[h] += output_errors[o]*W2[h][o];
          }
        }
        for (h=0; h<numHidden; h++) {
           hidden_errors[h] =
             hidden_errors[h]*sigmoidP(hidden[h]);
        }
        // update the hidden to output weights:
        for (o=0; o<numOutputs; o++) {
           for (h=0; h<numHidden; h++) {
              W2[h][o] +=
                 learningRate * output_errors[o] * hidden[h];
           }
        }
        // update the input to hidden weights:
        for (h=0; h<numHidden; h++) {
           for (i=0; i<numInputs; i++) {
               W1[i][h] +=
                  learningRate * hidden_errors[h] * inputs[i];
           }
        }
          for (o = 0; o < numOutputs; o++) {
              error += Math.abs(outs[o] - outputs[o]);
          }
        current_example++;
        if (current_example >= num_cases) current_example = 0;
  	  }
      return error;
    }
 
    protected float sigmoid(float x) {
        return (float) (1.0f / (1.0f + Math.exp((double) (-x))));
    }

    protected float sigmoidP(float x) {
        double z = sigmoid(x); 
        return (float) (z * (1.0f - z));
    }
}