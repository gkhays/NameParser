/*
 * NameParser - Parse names into first, last, initials, etc.
 * 
 * (C) Copyright 2016 Garve Hays and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Garve Hays
 */

package hays.gkh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameParser {
	
	private static List<String> nobiliaryParticleList = Arrays
			.asList(new String[] { "vere", "von", "van", "de", "del", "della",
					"di", "da", "pietro", "vanden", "du", "st.", "st", "la",
					"ter", "al", "ibn", "de la", "van der" });
	private static List<String> salutationList = Arrays.asList(new String[] {
			"mr", "master", "mister", "mrs", "miss", "ms", "dr", "rev", "fr" });
	private static List<String> suffixList = Arrays.asList(new String[] { "I",
			"II", "III", "IV", "V", "Senior", "Junior", "Jr", "JR", "Sr", "SR",
			"PhD", "APR", "RPh", "PE", "MD", "MA", "DMD", "CME", "Esq",
			"Esquire" });
	
	/**
	 * Test
	 */
	public static void main(String[] args) {
		String[] testNames = { "Von Fabella", "E. Pitney Bowes", "Dan Rather",
				"Dr. Jones", "Marcus Welby MD", "Ken Griffey Jr.",
				"Jack Jones M.D.", "E. Pluribus Unum", "Don R. Draper",
				"William S. Gates SR", "William S. Gates III",
				"Anthony de la Alpaca", "F. Murray Abraham",
				"Mr. Ted Knight Esquire", "Mrs. June Cleaver",
				"Mr. Robert Jones", "Ms. Cynthia Adams" };
		NameParser parser = new NameParser();
		for (String s : testNames) {
			parser.splitFullName(s);
			System.out.printf("[%4s] %10s | %2s %10s [%4s]\n",
					parser.getHonorific(), parser.getFirstName(),
					parser.getInitials(), parser.getLastName(),
					parser.getSuffix());
		}
	}

	private String firstName;
	private String honorific;
	private String initials;
	private String lastName;
	private String suffix;

	private String fixCase(String s) {
		String word = safeUpperCaseFirst(s, "-");
		word = safeUpperCaseFirst(s, Pattern.quote(".")); // '\\.'
		return word;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getHonorific() {
		return this.honorific;
	}
	
	public String getInitials() {
		return this.initials;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public Object getSuffix() {
		return this.suffix;
	}
	
	/**
	 * Detect compound last names such as "Von Fange."
	 * 
	 * Naturally there is a name for these kind of things; in this case it is
	 * nobiliary particle. See the Wikipedia article: <a
	 * href="https://en.wikipedia.org/wiki/Nobiliary_particle">Nobiliary
	 * particle</a>.
	 * 
	 * 
	 * @param s a {@link String} containing the name to test
	 * @return <code>true</code> if a compound name; otherwise false
	 */
	private boolean isCompoundLastName(String s) {
		String word = s.toLowerCase();
		for (String n : nobiliaryParticleList) {
			return (word.equals(n));
		}
		return false;
	}
	
	private boolean isHonorific(String s) {
		String word = s.replace(".", "").toLowerCase();
		for (String salutation : salutationList ) {
			return (word.equals(salutation));
		}
		return false;
	}
	
	private boolean isInitial(String s) {
		return s.length() == 1 || (s.length() == 2 && s.contains("."));
	}
	
	/**
	 * Check to see if the given {@link String} is in Pascal case, e.g.
	 * "McDonald."
	 * 
	 * @param s
	 *            the {@link String} to examine
	 * @return <code>true</code> if a match was found; false otherwise
	 */
	private boolean isPascalCase(String s) {
		// Considered (?<=[a-z])(?=[A-Z]).
		Pattern p = Pattern.compile("(?<=[a-z])(?=[A-Z])");
		Matcher m = p.matcher(s);
		return m.find();
	}

	private boolean isSuffix(String s) {
		String word = s.replace(".","");
		for (String suffix : suffixList) {
			if (word.equals(suffix)) {
				return true;
			}
		}
		return false;
	}
	
	private String parseHonorific(String s) {
		if (isHonorific(s) == false) {
			return "";
		}
		
		String word = s.replace(".", "").toLowerCase();
		String honorific;

		switch (word) {
		case "mr":
		case "master":
		case "mister":
			honorific = "Mr.";
			break;
		case "mrs":
			honorific = "Mrs.";
			break;
		case "miss":
		case "ms":
			honorific = "Ms.";
			break;
		case "dr":
			honorific = "Dr.";
			break;
		case "rev":
			honorific = "Rev.";
			break;
		case "fr":
			honorific = "Fr.";
			break;
		default:
			return "";
		}
		return honorific;
	}
	
	private String parseSuffix(String s) {
		String suffix = s;
		if (isSuffix(suffix)) {
			// Esquire should be abbreviated as "Esq." per Emily Post; see
			// http://emilypost.com/advice/the-correct-use-of-esquire/.
			if (suffix.toLowerCase().startsWith("esq")) {
				suffix = "Esq.";
			}
			return suffix;
		}
		return "";
	}
	
	/**
	 * Use this method if you don't have Java 8.
	 * 
	 * @param array
	 *            the {@link String} array containing the elements to join
	 * @param delimiter
	 *            the character used to join the elements
	 * @return the joined {@link String}
	 */
	private String quickStringJoin(String[] array, String delimiter) {
		int count = 0;
		StringBuilder sb = new StringBuilder();
		for (String s : array) {
			if (count == 0) {
				sb.append(s);
			} else {
				sb.append(delimiter).append(s);
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param word
	 * @param delimiter
	 * @return
	 * 
	 * @since 1.8
	 */
	private String safeUpperCaseFirst(String word, String delimiter) {
		String[] parts = word.split(delimiter);
		String[] words = new String[parts.length];
		// TODO: Ummm... Why not a conventional for-loop?
		int count = 0;
		for (String s : parts) {
			words[count] = isPascalCase(s) ? s : upperCaseFirst(s.toLowerCase());
			count++;
		}
		// Requires Java 8.
		return String.join(delimiter, words);
	}
	
	/**
	 * Splits a full name into the following parts:
	 * <ul>
	 * <li>Honorific, e.g. Mr., Mrs., Ms., etc.</li>
	 * <li>Given name or first name</li>
	 * <li>Surname or last name</li>
	 * <li>Given name or first name</li>
	 * <li>Suffix, e.g. II, Sr., PhD, etc.</li>
	 * </ul>
	 * 
	 * @param s
	 *            a {@link String} containing the full name to split
	 */
	public void splitFullName(String s) {
		// TODO: We can call splitFullName multiple times, which leaves some
		// baggage in the initial for each run. Quick hack below.
		this.initials = "";
		
		String fullName = s.trim();
		String[] unfilteredParts = fullName.split("\\s+");
		List<String> nameParts = new ArrayList<String>();
		
		for (String part : unfilteredParts) {
			if (part.contains("(") == false) {
				nameParts.add(part);
			}
		}
		
		int wordCount = nameParts.size();
		
		this.honorific = parseHonorific(nameParts.get(0));
		this.suffix = parseSuffix(nameParts.get(nameParts.size() - 1));
		
		int startIndex = (this.honorific.isEmpty() ? 0 : 1);
		int endIndex = (this.suffix.isEmpty() ? wordCount : wordCount - 1);
		
		String word;
		for (int i = startIndex; i < endIndex - 1; i++) {
			word = nameParts.get(i);
			
			// Move on to parsing the last name if we find an indicator of a
			// compound last name such as von, van, etc. We use i != startIndex
			// to allow for rare cases where an indicator is actually the first
			// name, like "Von Fabella."
			if (isCompoundLastName(word) && i != startIndex) {
				break;
			}
			
			// Is it a middle initial or part of the first name? If we start off
			// with an initial we count it as the first name.
			if (isInitial(word)) {
				// Is the initial the first word?
				if (i == startIndex) {
					// If so, look ahead to see if they go by their middle name,
					// e.g. "R. Jason Smith" => "Jason Smith" and "R." is stored
					// as an initial. Whereas "R. J. Smith" => "R. Smith" and
					// "J." is stored as an initial.
					if (isInitial(nameParts.get(i+1))) {
						this.firstName = word.toUpperCase();
					} else {
						this.initials = word.toUpperCase();
					}
				} else {
					this.initials = word.toUpperCase();
				}
			} else {
				this.firstName = fixCase(word);
			}
		}
		
		// Do we have more than a single word in our string?
		if (endIndex - startIndex > 1) {
			// Concatenate the last name.
			for (int j = 0; j < endIndex; j++) {
				this.lastName = fixCase(nameParts.get(j));
			}
		} else {
			// Otherwise, single word strings are assumed to be first names.
			this.firstName = fixCase(nameParts.get(0));
		}
	}
	
	/**
	 * Uppercase the first character in a given {@link String}.
	 * 
	 * @param s
	 *            the {@link String} upon which to operate
	 * @return a {@link String} with the first character in uppercase
	 */
	private String upperCaseFirst(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
