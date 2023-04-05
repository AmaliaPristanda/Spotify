from models.blacklist_orm import Blacklist
from base.sql_base import Session

def get_blacklist():
    session = Session()
    try:
        blacklist = session.query(Blacklist).all()
        lista = []
        for elem in blacklist:
            lista.append(elem.token)
        return ' '.join(lista)
    except Exception as exc:
        return "Exceptie: " + str(exc)

def verify_token_in_blacklist(token):
    result = get_blacklist()
    if "Exceptie" in result:
        return result
    else:
        lista = result.split(" ")
        if token in lista:
            return True
        else:
            return False

def add_token_to_blacklist(token):
    session = Session()
    try:
        elemBlack = session.query(Blacklist).filter(Blacklist.token == token).first()
        if elemBlack is not None:
            # exista deja
            return "Token-ul se afla deja in blacklist"
        else:
            elemToAdd = Blacklist(token)
            session.add(elemToAdd)
            session.commit()
            return "Token invalidat"
    except Exception as exc:
        return "Exceptie: " + str(exc)