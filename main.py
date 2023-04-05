<<<<<<< HEAD
from spyne import Application, rpc, ServiceBase, Integer, Double
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from SqlService import SqlService
from wsgi_cors_middleware import CorsMiddleware

#configurez aplicatia web spy care sa raspunda
#spatiu de nume asociat serviciului respectiv = services.calculator.soap

application = Application([SqlService], 'services.spotify.soap',
                        in_protocol=Soap11(validator='lxml'), #protocolul de transport pentru datele input
                        out_protocol=Soap11()) #protocolul pentru datele output

#inregostrez aplicatia in cadrul serverului
wsgi_application = WsgiApplication(application)


if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    finalApplication = CorsMiddleware(
        wsgi_application,
        origin='*',
        methods=['POST', 'PUT'],
        headers=['Content-Type', 'Authorization']
    )
    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, finalApplication)
=======
from spyne import Application, rpc, ServiceBase, Integer, Double
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from SqlService import SqlService
from wsgi_cors_middleware import CorsMiddleware

#configurez aplicatia web spy care sa raspunda
#spatiu de nume asociat serviciului respectiv = services.calculator.soap

application = Application([SqlService], 'services.spotify.soap',
                        in_protocol=Soap11(validator='lxml'), #protocolul de transport pentru datele input
                        out_protocol=Soap11()) #protocolul pentru datele output

#inregostrez aplicatia in cadrul serverului
wsgi_application = WsgiApplication(application)


if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    finalApplication = CorsMiddleware(
        wsgi_application,
        origin='*',
        methods=['POST', 'PUT'],
        headers=['Content-Type', 'Authorization']
    )
    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, finalApplication)
>>>>>>> e7eabf4 (IDM module)
    server.serve_forever()