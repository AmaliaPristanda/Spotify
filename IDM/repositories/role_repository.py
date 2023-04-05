<<<<<<< HEAD
import logging

from models.role_orm import Role
from base.sql_base import Session

#doua surse de date pentru tabelele principale

def get_roles():
    session = Session()
    try:
        roles = session.query(Role).all()
        rezultat = ""
        for role in roles:
            rezultat += role.rolename + "\n"
        return rezultat
    except Exception as exc:
        return "Exceptie: " + str(exc)


def get_user_role():
    session = Session()
    try:
        roles = session.query(Role).all()
        rezultat = ""
        for role in roles:
            rezultat += role.rolename + "\n"
        return rezultat
    except Exception as exc:
        return "Exceptie: " + str(exc)

def create_role(rolename):
    session = Session()
    role = Role(rolename)
    try:
        rol = session.query(Role).filter(Role.rolename == rolename).first()
        if rol is not None:
            return "Rolul exista deja"
        else:
            session.add(role)
            session.commit()
            session.close()
            return "Rolul a fost creat cu succes!"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def get_role_name(role_id):
    session = Session()
    try:
        rol = session.query(Role).filter(Role.ID == role_id).first()
        if rol is None:
            return "Rolul nu exista"
        else:
            return rol.rolename
    except Exception as exc:
=======
import logging

from models.role_orm import Role
from base.sql_base import Session

#doua surse de date pentru tabelele principale

def get_roles():
    session = Session()
    try:
        roles = session.query(Role).all()
        rezultat = ""
        for role in roles:
            rezultat += role.rolename + "\n"
        return rezultat
    except Exception as exc:
        return "Exceptie: " + str(exc)


def get_user_role():
    session = Session()
    try:
        roles = session.query(Role).all()
        rezultat = ""
        for role in roles:
            rezultat += role.rolename + "\n"
        return rezultat
    except Exception as exc:
        return "Exceptie: " + str(exc)

def create_role(rolename):
    session = Session()
    role = Role(rolename)
    try:
        rol = session.query(Role).filter(Role.rolename == rolename).first()
        if rol is not None:
            return "Rolul exista deja"
        else:
            session.add(role)
            session.commit()
            session.close()
            return "Rolul a fost creat cu succes!"
    except Exception as exc:
        return "Exceptie: " + str(exc)


def get_role_name(role_id):
    session = Session()
    try:
        rol = session.query(Role).filter(Role.ID == role_id).first()
        if rol is None:
            return "Rolul nu exista"
        else:
            return rol.rolename
    except Exception as exc:
>>>>>>> e7eabf4 (IDM module)
        return "Exceptie: " + str(exc)