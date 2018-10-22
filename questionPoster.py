import requests

r = requests.post("http://royaltrashapp.azurewebsites.net/api/Quiz/PostQuiz",
                  json={
                      "question": "FN:s deklaration om de mänskliga rättigheterna antogs år",
                      "C1answer": "1948",
                      "C2answer": "1925",
                      "C3answer": "1994",
                      "C4answer": "2001"
                        })

print(r.status_code, r.reason)
