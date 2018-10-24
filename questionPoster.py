import requests

r = requests.post("http://royaltrashapp.azurewebsites.net/api/Quiz/PostQuiz",
                  json={
                      "question": "",
                      "C1answer": "",
                      "C2answer": "",
                      "C3answer": "",
                      "C4answer": ""
                        })

print(r.status_code, r.reason)
