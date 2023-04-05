<<<<<<< HEAD
from models.user_orm import User
from models.role_orm import Role
from models.users_roles_orm import user_roles_relationship
from base.sql_base import Session
from sqlalchemy import select, update, delete, values
from sqlalchemy.orm import column_property
from datetime import datetime
from datetime import timedelta
from jose import jws
from repositories import blacklist_repository
from repositories import role_repository
import json
import uuid

#401 token expirat, lipseste, credentiale gresite

def is_user_administrator(result):
    lista = result.split("-")
    userID = int(lista[0])
    roluriUser = lista[1].split(" ")

    isUserAdministrator = False
    for rol in roluriUser:
        if role_repository.get_role_name(int(rol)) == "administratorAplicatie":
            isUserAdministrator = True
            break
    return isUserAdministrator

def create_user(username, password, token):
    session = Session()
    try:
        result = autorizare(token)
        #return result
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result) == True:
                utilizator = session.query(User).filter(User.username == username).first()
                if utilizator is not None:
                    # exista deja
                    return "Eroare: username-ul este luat deja"
                else:
                    user = User(username, password)
                    session.add(user)
                    session.commit()
                    return "User-ul nou creat: " + user.username + "-" + user.password
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def get_users(token):
    session = Session()
    try:
        result = autorizare(token)
        # return result
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                users = session.query(User).all()
                rezultat = ""
                for user in users:
                    rezultat += "User: username = " + user.username + ", parola = " + user.password + ", rol/uri = "
                    for role in user.roles:
                        rezultat += role.rolename + " "
                    rezultat += "\n\n"
                return rezultat
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"

    except Exception as exc:
        return "Exceptie: " + str(exc)


def change_user_password(username, password, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            lista = result.split("-")
            userID = int(lista[0])

            #trebuie sa vad daca utilizatorul exista mai intai
            utilizator = session.query(User).filter(User.username == username).first()
            if utilizator is not None:
                if userID == utilizator.ID:
                    sql1 = update(User).values(password=password).where(User.username == username)
                    session.execute(sql1)
                    session.commit()
                    return "Parola a fost schimbata cu succes"
                else:
                    return "Nu se poate efectua operatia - insuficiente credentiale"
            else:
                return "Eroare: utilizatorul nu exista in baza de date"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def delete_user(username, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                utilizator = session.query(User).filter(User.username == username).first()
                if utilizator is not None:
                    sql1 = delete(User).where(User.username == username)
                    session.execute(sql1)
                    session.commit()
                    return "Utilizatorul a fost sters cu succes!"
                else:
                    return "Eroare: utilizatorul nu a fost gasit in baza de date"
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def add_role_to_user(username, rolename, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                user = session.query(User).filter(User.username == username).first()
                role = session.query(Role).filter(Role.rolename == rolename).first()
                if user is not None and role is not None:
                    user_id = user.ID
                    role_id = role.ID

                    userAlreadyHasThatRole = False
                    for role in user.roles:
                        if role.ID == role_id:
                            userAlreadyHasThatRole = True

                    if userAlreadyHasThatRole == False:
                        stmt = user_roles_relationship.insert().values(ID_U=user_id, ID_R=role_id)
                        session.execute(stmt)
                        session.commit()
                        return "Rolul a fost adaugat cu succes utilizatorului\n"
                    else:
                        return "Utilizatorul are deja rolul de " + rolename + "\n"
                else:
                    eroare = "Eroare: \n"
                    if user is None:
                        eroare += "\tUtilizatorul nu se afla in baza de date\n"
                    if role is None:
                        eroare += "\tNu exista rolul in baza de date\n"
                    return eroare
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def change_user_role(username, old_rolename, new_rolename, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                user = session.query(User).filter(User.username == username).first()
                new_role = session.query(Role).filter(Role.rolename == new_rolename).first()
                old_role = session.query(Role).filter(Role.rolename == old_rolename).first()

                if user is not None and new_role is not None and old_role is not None:
                    user_id = user.ID
                    new_role_id = new_role.ID
                    old_role_id = old_role.ID

                    stmt = (update(user_roles_relationship).values(ID_R=new_role_id)).where(
                        user_roles_relationship.c.ID_U == user_id,
                        user_roles_relationship.c.ID_R == old_role_id)
                    session.execute(stmt)
                    session.commit()

                    return "Rolul utilizatorului a fost schimbat cu succes"
                else:
                    eroare = "Eroare: \n"
                    if user is None:
                        eroare += "\tUtilizatorul nu exista in baza de date\n"
                    if new_role is None or old_role is None:
                        eroare += "\tRolul nu exista in baza de date\n"
                    return eroare
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)



def get_user_roles(user):
    try:
        #stiu sigur ca user-ul exista in baza de date
        roluri = ""
        for role in user.roles:
            roluri += str(role.ID) + " "
        #ca sa elimin ultimul spatiu
        return roluri[:-1]

    except Exception as exc:
       return "Exceptie: " + str(exc)


def login(username, password):
    session = Session()
    try:
        user = session.query(User).filter(User.username == username, User.password == password).first()
        if user is not None:
            # creez jws
            expira = datetime.now() + timedelta(hours=1)

            roles_id = get_user_roles(user)
            if "Exceptie" not in roles_id:
                payload = {
                    "iss": "http://127.0.0.1:8000",
                    "sub": str(user.ID) + ":" + user.username,
                    "exp": expira.strftime("%m/%d/%y %H:%M:%S"),
                    "jti": str(uuid.uuid1()),
                    "role": roles_id
                }

                token = jws.sign(payload, 'secret', algorithm='HS256')
                return token
            else:
                return roles_id
        else:
            return "Eroare: username/parola gresite"
    except Exception as exp:
        return "Exceptie:" + str(exp)


def autorizare(token):
    session = Session()
    try:
        #verific daca nu se fala in blacklist
        if blacklist_repository.verify_token_in_blacklist(token) == False:
            decodat = jws.verify(token, 'secret', algorithms=['HS256'])
            #return decodat
            jsonDecodat = json.loads(decodat)
            # verific daca token-ul nu este expirat

            userID = int(jsonDecodat["sub"].split(":")[0])
            dataExpirarii = datetime.strptime(jsonDecodat["exp"], "%m/%d/%y %H:%M:%S")
            roluri = jsonDecodat["role"]

            if dataExpirarii > dataExpirarii:
                #return "token expirat"
                return blacklist_repository.add_token_to_blacklist(token)

            user = session.query(User).filter(User.ID == userID).first()
            if user is not None:
                # verific daca exista asociera dintre user si role din token-ul primit
                user_roles = get_user_roles(user)
                if user_roles == roluri:
                    return str(userID) + "-" + user_roles
                else:
                    #return "rolurile primite nu sunt aceleasi cu cele din token"
                    return blacklist_repository.add_token_to_blacklist(token)
            else:
                #return "nu a fost gasit userul"
                return blacklist_repository.add_token_to_blacklist(token)
        else:
            #return "token in blacklist"
            return blacklist_repository.add_token_to_blacklist(token)

    except Exception as exp:
        return "Exceptie: " + str(exp)

def logout(token):
=======
from models.user_orm import User
from models.role_orm import Role
from models.users_roles_orm import user_roles_relationship
from base.sql_base import Session
from sqlalchemy import select, update, delete, values
from sqlalchemy.orm import column_property
from datetime import datetime
from datetime import timedelta
from jose import jws
from repositories import blacklist_repository
from repositories import role_repository
import json
import uuid

#401 token expirat, lipseste, credentiale gresite

def is_user_administrator(result):
    lista = result.split("-")
    userID = int(lista[0])
    roluriUser = lista[1].split(" ")

    isUserAdministrator = False
    for rol in roluriUser:
        if role_repository.get_role_name(int(rol)) == "administratorAplicatie":
            isUserAdministrator = True
            break
    return isUserAdministrator

def create_user(username, password, token):
    session = Session()
    try:
        result = autorizare(token)
        #return result
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result) == True:
                utilizator = session.query(User).filter(User.username == username).first()
                if utilizator is not None:
                    # exista deja
                    return "Eroare: username-ul este luat deja"
                else:
                    user = User(username, password)
                    session.add(user)
                    session.commit()
                    return "User-ul nou creat: " + user.username + "-" + user.password
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def get_users(token):
    session = Session()
    try:
        result = autorizare(token)
        # return result
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                users = session.query(User).all()
                rezultat = ""
                for user in users:
                    rezultat += "User: username = " + user.username + ", parola = " + user.password + ", rol/uri = "
                    for role in user.roles:
                        rezultat += role.rolename + " "
                    rezultat += "\n\n"
                return rezultat
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"

    except Exception as exc:
        return "Exceptie: " + str(exc)


def change_user_password(username, password, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            lista = result.split("-")
            userID = int(lista[0])

            #trebuie sa vad daca utilizatorul exista mai intai
            utilizator = session.query(User).filter(User.username == username).first()
            if utilizator is not None:
                if userID == utilizator.ID:
                    sql1 = update(User).values(password=password).where(User.username == username)
                    session.execute(sql1)
                    session.commit()
                    return "Parola a fost schimbata cu succes"
                else:
                    return "Nu se poate efectua operatia - insuficiente credentiale"
            else:
                return "Eroare: utilizatorul nu exista in baza de date"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def delete_user(username, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                utilizator = session.query(User).filter(User.username == username).first()
                if utilizator is not None:
                    sql1 = delete(User).where(User.username == username)
                    session.execute(sql1)
                    session.commit()
                    return "Utilizatorul a fost sters cu succes!"
                else:
                    return "Eroare: utilizatorul nu a fost gasit in baza de date"
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def add_role_to_user(username, rolename, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                user = session.query(User).filter(User.username == username).first()
                role = session.query(Role).filter(Role.rolename == rolename).first()
                if user is not None and role is not None:
                    user_id = user.ID
                    role_id = role.ID

                    userAlreadyHasThatRole = False
                    for role in user.roles:
                        if role.ID == role_id:
                            userAlreadyHasThatRole = True

                    if userAlreadyHasThatRole == False:
                        stmt = user_roles_relationship.insert().values(ID_U=user_id, ID_R=role_id)
                        session.execute(stmt)
                        session.commit()
                        return "Rolul a fost adaugat cu succes utilizatorului\n"
                    else:
                        return "Utilizatorul are deja rolul de " + rolename + "\n"
                else:
                    eroare = "Eroare: \n"
                    if user is None:
                        eroare += "\tUtilizatorul nu se afla in baza de date\n"
                    if role is None:
                        eroare += "\tNu exista rolul in baza de date\n"
                    return eroare
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def change_user_role(username, old_rolename, new_rolename, token):
    session = Session()
    try:
        result = autorizare(token)
        if result == "Token invalidat" or "Exceptie" in result:
            return result
        else:
            if is_user_administrator(result):
                user = session.query(User).filter(User.username == username).first()
                new_role = session.query(Role).filter(Role.rolename == new_rolename).first()
                old_role = session.query(Role).filter(Role.rolename == old_rolename).first()

                if user is not None and new_role is not None and old_role is not None:
                    user_id = user.ID
                    new_role_id = new_role.ID
                    old_role_id = old_role.ID

                    stmt = (update(user_roles_relationship).values(ID_R=new_role_id)).where(
                        user_roles_relationship.c.ID_U == user_id,
                        user_roles_relationship.c.ID_R == old_role_id)
                    session.execute(stmt)
                    session.commit()

                    return "Rolul utilizatorului a fost schimbat cu succes"
                else:
                    eroare = "Eroare: \n"
                    if user is None:
                        eroare += "\tUtilizatorul nu exista in baza de date\n"
                    if new_role is None or old_role is None:
                        eroare += "\tRolul nu exista in baza de date\n"
                    return eroare
            else:
                return "Nu se poate efectua operatia - insuficiente credentiale"
    except Exception as exc:
        return "Exceptie: " + str(exc)



def get_user_roles(user):
    try:
        #stiu sigur ca user-ul exista in baza de date
        roluri = ""
        for role in user.roles:
            roluri += str(role.ID) + " "
        #ca sa elimin ultimul spatiu
        return roluri[:-1]

    except Exception as exc:
       return "Exceptie: " + str(exc)


def login(username, password):
    session = Session()
    try:
        user = session.query(User).filter(User.username == username, User.password == password).first()
        if user is not None:
            # creez jws
            expira = datetime.now() + timedelta(hours=1)

            roles_id = get_user_roles(user)
            if "Exceptie" not in roles_id:
                payload = {
                    "iss": "http://127.0.0.1:8000",
                    "sub": str(user.ID) + ":" + user.username,
                    "exp": expira.strftime("%m/%d/%y %H:%M:%S"),
                    "jti": str(uuid.uuid1()),
                    "role": roles_id
                }

                token = jws.sign(payload, 'secret', algorithm='HS256')
                return token
            else:
                return roles_id
        else:
            return "Eroare: username/parola gresite"
    except Exception as exp:
        return "Exceptie:" + str(exp)


def autorizare(token):
    session = Session()
    try:
        #verific daca nu se fala in blacklist
        if blacklist_repository.verify_token_in_blacklist(token) == False:
            decodat = jws.verify(token, 'secret', algorithms=['HS256'])
            #return decodat
            jsonDecodat = json.loads(decodat)
            # verific daca token-ul nu este expirat

            userID = int(jsonDecodat["sub"].split(":")[0])
            dataExpirarii = datetime.strptime(jsonDecodat["exp"], "%m/%d/%y %H:%M:%S")
            roluri = jsonDecodat["role"]

            if dataExpirarii > dataExpirarii:
                #return "token expirat"
                return blacklist_repository.add_token_to_blacklist(token)

            user = session.query(User).filter(User.ID == userID).first()
            if user is not None:
                # verific daca exista asociera dintre user si role din token-ul primit
                user_roles = get_user_roles(user)
                if user_roles == roluri:
                    return str(userID) + "-" + user_roles
                else:
                    #return "rolurile primite nu sunt aceleasi cu cele din token"
                    return blacklist_repository.add_token_to_blacklist(token)
            else:
                #return "nu a fost gasit userul"
                return blacklist_repository.add_token_to_blacklist(token)
        else:
            #return "token in blacklist"
            return blacklist_repository.add_token_to_blacklist(token)

    except Exception as exp:
        return "Exceptie: " + str(exp)

def logout(token):
>>>>>>> e7eabf4 (IDM module)
    return blacklist_repository.add_token_to_blacklist(token)