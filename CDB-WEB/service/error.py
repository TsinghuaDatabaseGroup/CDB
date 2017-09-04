errors = {
    'JSON_PARSE': (1001, 'JSON parse error'),
    'ARGUMENTS': (1002, 'Arguments error'),
    'VALIDATE_CODE_EXPIRED': (2001, 'Validate code expired'),
    'VALIDATE_CODE_WRONG': (2002, 'Validate code is wrong'),
    'USER_EXISTS': (2003, 'Username exists'),
    'USERNAME_PASSWORD_WRONG': (2005, 'Username or password is wrong'),
    'NO_SUCH_USER': (2006, 'No such user'),
    'PASSWORD_WRONG': (2007, 'Password is wrong'),
    'CHECK ERROR': (3001, 'No result fits qurey'),
    'PAGE OVERFLOW': (3002, 'Page is not valid'),
    'LABEL ERROR': (3003, 'some label checked is not included'),
    'NO_SUCH_CITY': (4001, 'city not found'),
    'NO_SUCH_ATTRACTION': (4002, 'attraction not found'),
    'NO_SUCH_COUNTRY': (4003, 'country not found'),
    'NO_IMG_UPLOADED': (5001, 'no image file is uploaded'),
    'NOT_VALID_IMAGE': (5002, 'the uploaded file is not image file'),
    'SQL_ERROR': (6001, 'SQL execution error'),
    'PERMISSION DENIED': (9000, 'Not authenticated to access this resource')
}


def error(key, msg=None, remark=None):
    if key in errors:
        e = errors[key]
        if not msg:
            return {'succeed':False, 'errno': e[0], 'errmsg': e[1], 'remark':remark}
        else:
            return {'succeed':False, 'errno': e[0], 'errmsg': msg, 'remark':remark}
    else:
        if not msg:
            msg = "Unknown"
        return { 'succeed':False, 'errno': None, 'errmsg': msg, 'remark':remark}

def success(**kwargs):
    res = {'success': 1}
    if kwargs:
        res.update(kwargs)
    return res

def maybe(res, key, **kwargs):
    if res:
        return success(**kwargs)
    else:
        return error(key)
