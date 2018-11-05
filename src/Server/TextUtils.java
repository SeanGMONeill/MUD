package Server;

import java.util.List;

public class TextUtils {

	/*
	 * @require conjunction is an english conjunction, i.e. "and", "or"
	 * @return input as a conjuncted string with commas, or empty string if input is empty.
	 */
	public static String listToEnglish(List<String> input, String conjunction) {
		String listString = "";
		if(input.size() == 0) {
			return listString;
		}
		else {
			for(int i = 0; i < input.size(); i++) {
				listString = listString + input.get(i);
				if(i == input.size()-2) {
					if(input.size() == 2) {
						listString = listString + " " + conjunction + " ";
					}
					else if(input.size() > 2) {
						listString = listString + ", " + conjunction + " ";
					}
				}
				else if(i != input.size()-1) {
					listString = listString + ", ";
				}
			}
		}
		
		return listString;
	}
	
}
