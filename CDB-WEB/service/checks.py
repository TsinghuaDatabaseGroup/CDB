import validators

import pytz

def optional(func):
    def optional_func(s):
        if s is None:
            return True
        return func(s)
    return optional_func


def list_check(min_len, max_len, item_checker):
    def list_check_func(s):
        if not isinstance(s, list):
            print s, 'is not list'
            return False
        length = len(s)
        if length < min_len or length > max_len:
            print length, ' length invalid'
            return False
        return all([item_checker(i) for i in s])
    return list_check_func

def is_trip_item():
    return enum_in(
        json_check(
            type=equals("start"),
            timezone=is_timezone,
            city_id=len_in(1,256),
            city_name=len_in(1,512),
            city_url_name=len_in(1,512),
            start_time=is_timestamp
        ),
        json_check(
            type=equals("city"),
            city_id=len_in(1,256),
            city_name=len_in(1,512),
            city_url_name=len_in(1,512),
            days=list_check(1, 256, json_check(
                items=list_check(0,256, enum_in(
                    json_check(
                        type=equals("transport"),
                        timezone=is_timezone,
                        start_time=is_timestamp,
                        end_time=is_timestamp,
                        vehicle=enum_in(
                            equals("car"),
                            equals("airplane"),
                            equals("train"),
                            equals("ship"))
                        ),
                    json_check(
                        type=equals("attraction"),
                        timezone=is_timezone,
                        start_time=is_timestamp,
                        end_time=is_timestamp,
                        attraction_id=len_in(1,256),
                        attraction_name=len_in(1,512),
                        attraction_url_name=len_in(1,512)
                        )
                    )
                ))
            )
        ),
        json_check(
            type=equals("return"),
            timezone=is_timezone,
            city_id=len_in(1,256),
            city_name=len_in(1,512),
            city_url_name=len_in(1,512),
            start_time=is_timestamp
        )
    )


def is_timestamp(s):
    return int_in(0, 10000000000)

def all_right(s):
    return True

def enum_in(*checkers):
    def enum_in_func(s):
        print " ++ start enum ++ "
        for checker in checkers:
            if checker(s):
                print " ++ enum true ++"
                return True
        print " ++ enum false ++", s
        return False
    return enum_in_func

def equals(x):
    def equals_func(s):
        return x == s
    return equals_func

def json_check(**checkers):
    def json_check_func(s):
        if not isinstance(s, dict):
            print s, 'is not dict'
            return False
        for k in s:
            if k not in checkers:
                print k, 'is not key'
                return False
        tmp = all([check_one_field(s, k, v) for k, v in checkers.iteritems()])
        #print tmp
        return tmp
    return json_check_func

def loose_json_check(**checkers):
    def json_check_func(s):
        if not isinstance(s, dict):
            print s, 'is not dict'
            return False
        tmp = all([check_one_field(s, k, v) for k, v in checkers.iteritems()])
        #print tmp
        return tmp
    return json_check_func

def check_one_field(json_obj, key, checker, errs=None):
    if key in json_obj:
        res = checker(json_obj[key])
        if not res:
            print key, res
            if errs != None:
                errs.append(key)
        return res
    print key, "not found"
    res = checker(None)
    if (not res) and (errs != None):
        errs.append(key)
    return res


def is_email(s):
    return len_in(5, 128)(s) and validators.email(s) 

def is_timezone(s):
    return s in pytz.all_timezones

def is_url(s):
    return len_in(5, 256)(s)


def is_price(s):
    return isinstance(s, int) or isinstance(s, float)


def int_in(min_int, max_int):
    def int_in_func(s):
        if not is_int(s):
            return False
        return s >= min_int and s <= max_int
    return int_in_func


def len_in(min_len, max_len):
    def len_in_func(s):
        if not is_str(s):
            return False
        s = s.strip()
        return len(s) >= min_len and len(s) <= max_len
    return len_in_func


def not_empty(s):
    return is_str(s) and s.strip()


def is_str(s):
    return isinstance(s, basestring)


def is_int(s):
    return isinstance(s, int) or isinstance(s, long)

def is_positive(s):
    return isinstance(s, int) and s > 0

def is_info(s):
    return s in ['overview', 'visa', 'religion', 'custom', 'food', 'currency']

def is_transportation(s):
    return s in ['overview', 'flight', 'train', 'car']

def is_str_list(x):
    return isinstance(x, list) and all(map(is_str, x))

def is_bool(x):
    return x in [True, False]
