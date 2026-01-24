Create an AI_USAGE.md file that includes:
Which AI tools you used (e.g., ChatGPT, Claude, GitHub Copilot)  -- Chatgpt 

Key prompts/questions you asked
		1.Since this is browser website , How can we fetch data from this website , does they expose any REST API ?
		2.what is SDMX API , is there any libraries we can use to fetch directly data and how to integrate this in java code
		3.what is way for streaming large data as i was hetting out of memory error 
		4.How to write parser for the xml 
		5.Handle large XML responses from the Bundesbank in a reactive service
		6 why flux 
		
Relevant AI responses that shaped your solution :
		Below options were suggested : 
		1.Download via the Website 
		2.Using SDMX REST API 
		3.Using SDMX Libraries
		4.Web Scraping 

Your reasoning for accepting, modifying, or rejecting AI suggestions--
		I checked generated xml after calling excahnge rate API, whcih was around 1.5Gb for 3 days
		Among these above options , Considering amount of data received in xml ,Using XML libraries seems better approach as:
		Libraries can be dirctly used in code like service layer and libraries can parse xml easilly considering error handling
        Refered code for parser 
		After trying to generate data for 10 days , i got out of memory error for multiple reasons 
		Reactive programming with Flux allows each currency rate to be processed as soon as it is parsed, improving performance and memory usage.
		The parser produces CurrencyConversionRate objects as a Flux, allowing downstream reactive processing.
		Errors during fetching or parsing are propagated via the reactive stream, ensuring safe and testable error handling.
		This approach ensures memory efficiency, scalability, and real-time processing of currency rates.
		
		