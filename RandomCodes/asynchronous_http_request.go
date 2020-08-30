package main
import (
	"fmt"
	"net/http"
	"crypto/tls"
	"time"
)

func checkURL(url_r string , finished chan <- bool) {
	//Skip SSL verification
	transport := &http.Transport{}
	transport.TLSClientConfig = &tls.Config{InsecureSkipVerify: true}

	//Add timeout of 3 seconds
	client := &http.Client{Timeout: 3 * time.Second}
	client.Transport = transport

	//Create a new HTTP GET request
	req , err := http.NewRequest("GET" , url_r , nil)
	if err != nil {
		finished <- true
		return 
	}

	//Send the HTTP request
	resp , err := client.Do(req)
	if err != nil {
		finished <- true
		return 
	}

	//Print the status code
	fmt.Printf("[%d] : %s\n" , resp.StatusCode , url_r)
	
	//Everytime the function finishes, send boolean code to main
	finished <- true
}

func main() {
	urls := []string {
		"https://www.easyjet.com/",
		"https://www.skyscanner.de/",
		"https://www.ryanair.com",
		"https://wizzair.com/",
		"https://www.swiss.com/",
		"https://testninjaxscorphub.com/",
	}
	no_asynchronous_calls := 0
	finished := make(chan bool)
	for _ , url_i := range urls {
		go checkURL(url_i , finished)
		no_asynchronous_calls++
	}
	no_asynchronous_calls_completed := 0
	for {
		//if all goroutines have completed execution, close the channel and exit
		if no_asynchronous_calls_completed == no_asynchronous_calls {
			close(finished)
			return
		} else if <- finished == true {
			no_asynchronous_calls_completed++
		}
	}
}
