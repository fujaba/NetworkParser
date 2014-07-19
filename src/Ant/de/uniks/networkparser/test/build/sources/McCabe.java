package de.uniks.networkparser.test.build.sources;


public class McCabe {
	private int mcCabe=1;
	public void finish(MethodItem item){
		String allText = item.toString();
        allText=allText.toLowerCase();
        allText=allText.replaceAll("\n", "");
        allText=allText.replaceAll("\t", "");
        allText=allText.replaceAll(" ", "");
        allText=allText.replaceAll("<", "(");
        allText=allText.replaceAll(">", ")");
        evaluate(allText);
	}
	
	
	public void evaluate(String allText)
    {
            int index=0;
            for(int i = 0; i < allText.length(); i++)
            {
                    index = allText.indexOf("if(", index);
                    if(index != -1)
                    {
                            if(checkQuotes(allText, index))
                            {
                                    mcCabe++;
                                    index+=1;
                            }
                            else
                                    index+=1;
                    }
                    else
                            i = allText.length();

            }
            
            index = 0;
            for(int i = 0; i < allText.length(); i++)
            {
                    index = allText.indexOf("do{", index);
                    if(index != -1)
                    {
                            if(checkQuotes(allText, index))
                            {
                                    mcCabe++;
                                    index+=1;
                            }
                            else
                                    index+=1;
                    }
                    else
                            i = allText.length();

            }
            
            index = 0;
            for(int i = 0; i < allText.length(); i++)
            {
                    index = allText.indexOf("while(", index);
                    if(index != -1)
                    {
                            if(checkQuotes(allText, index))
                            {
                                    
                                    mcCabe++;
                                    index+=1;
                            }
                            else
                                    index+=1;
                    }
                    else
                            i = allText.length();

            }
            
            index = 0;
            for(int i = 0; i < allText.length(); i++)
            {
                    index = allText.indexOf("&&", index);
                    if(index != -1)
                    {
                            if(checkQuotes(allText, index))
                            {
                                    
                                    mcCabe++;
                                    index+=1;
                            }
                            else
                                    index+=1;
                    }
                    else
                            i = allText.length();

            }
    }

    public boolean checkQuotes(String allText, int index)
    {
            int quote = 0;
            for(int i = 0; i < index; i++)
            {
                    char nextChar = allText.charAt(i);
                    if(nextChar == '\"')
                            quote++;
            }

            if(quote%2==0)
            {
                    return true;
            }
            else
            {
                    return false;
            }
    }
    
	public int getMcCabe() {
		return mcCabe;
	}
}
