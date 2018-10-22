import requests

r = requests.post("http://royaltrashapp.azurewebsites.net/api/Quiz/PostQuiz",
                  json={
                      "question": "Om man förbränner 1 kilo bensin ger det upphov till...",
                      "C1answer": "cirka 3 kilo koldioxid",
                      "C2answer": "cirka 1 kilo koldioxid",
                      "C3answer": "cirka 5 kilo koldioxid",
                      "C4answer": "cirka 10 kilo koldioxid"
                        })

print(r.status_code, r.reason)
