from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

Base = declarative_base()
#de aici mentin pana la ://
#remote-admin inlocuiesc cu user de dreoturi update, create, insert, delete
#idm -> auth-db
engine = create_engine('mariadb+mariadbconnector://auth-user:passwdauthuser@192.168.56.10:3306/auth-db')
Session = sessionmaker(bind=engine)
