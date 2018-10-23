import requests

r = requests.post("http://royaltrashapp.azurewebsites.net/api/Quiz/PostQuiz",
                  json={
                      "question": "Hur lång tid tar det för en flaska att brytas ned av naturen?",
                      "C1answer": "4000 år",
                      "C2answer": "300 år",
                      "C3answer": "25000 år",
                      "C4answer": "10000 år"
                        })

print(r.status_code, r.reason)
