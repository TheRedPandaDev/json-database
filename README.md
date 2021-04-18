# JSON Database server
A concurrent database server based on a single JSON file with the ability to store JSON objects and manipulate any individual nested values

## Program arguments (Client)

### -t {request_type}
Specify the type of the request

Request types:
#### set
Sets the specified value to the specified key

#### get
Gets value of the specified key

#### delete
Deletes entry by key

### -k {key}
Specify the key

### -v {value}
Specify the value in case the request type is "set"

### -in {file_name}
Specify the file name of a file containing your request.  
**If the -in argument followed by the file name was provided, all the previous arguments will be ignored.**

## Example

    > java Main -t set -k 1 -v "Hello world!" 
    Client started!
    Sent: {"type":"set","key":"1","value":"Hello world!"}
    Received: {"response":"OK"}
    > java Main -in setFile.json 
    Client started!
    Sent:
    {
       "type":"set",
       "key":"person",
       "value":{
          "name":"Elon Musk",
          "car":{
             "model":"Tesla Roadster",
             "year":"2018"
          },
          "rocket":{
             "name":"Falcon 9",
             "launches":"87"
          }
       }
    }
    Received: {"response":"OK"}
    > java Main -in getFile.json 
    Client started!
    Sent: {"type":"get","key":["person","name"]}
    Received: {"response":"OK","value":"Elon Musk"}
    > java Main -in updateFile.json 
    Client started!
    Sent: {"type":"set","key":["person","rocket","launches"],"value":"88"}
    Received: {"response":"OK"}
    > java Main -in secondGetFile.json 
    Client started!
    Sent: {"type":"get","key":["person"]}
    Received:
    {
       "response":"OK",
       "value":{
          "name":"Elon Musk",
          "car":{
             "model":"Tesla Roadster",
             "year":"2018"
          },
          "rocket":{
             "name":"Falcon 9",
             "launches":"88"
          }
       }
    }
    > java Main -in deleteFile.json 
    Client started!
    Sent: {"type":"delete","key":["person","car","year"]}
    Received: {"response":"OK"}
    > java Main -in secondGetFile.json 
    Client started!
    Sent: {"type":"get","key":["person"]}
    Received:
    {
       "response":"OK",
       "value":{
          "name":"Elon Musk",
          "car":{
             "model":"Tesla Roadster"
          },
          "rocket":{
             "name":"Falcon 9",
             "launches":"88"
          }
       }
    }
    > java Main -t exit 
    Client started!
    Sent: {"type":"exit"}
    Received: {"response":"OK"}
