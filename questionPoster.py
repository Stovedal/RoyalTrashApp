import requests

r = requests.post("http://royaltrashapp.azurewebsites.net/api/Quiz/PostQuiz",
                  json={
                      "question": "Hur lång tid tar det för en glasflaska att brytas ned av naturen?",
                      "C1answer": "",
                      "C2answer": "",
                      "C3answer": "",
                      "C4answer": ""
                        })

print(r.status_code, r.reason)
