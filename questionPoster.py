import requests

r = requests.post("http://royaltrashapp.azurewebsites.net/api/Quiz/PostQuiz",
                  json={"Id": 0, 
                        "question": "Begreppet peak oil syftar på \"största möjliga oljekonsumtion\" ?",
                  "C1answer": "Falskt", 
                  "C2answer": "Sant", 
                  "C3answer": "", 
                  "C4answer": ""})

print(r.status_code, r.reason)
