from suds.client import Client
c = Client("http://localhost:8000/?wsdl")

#c.service.createRole("administratorAplicatie")
#c.service.createRole("contentManager")
#c.service.createRole("artist")
#c.service.createRole("client")


token = c.service.autentificare("amalia", "amalia")

print(token)
#print(c.service.addRoleToUser("amalia", "contentManager", token))
#print(c.service.createUser("mada", "mada", token))
