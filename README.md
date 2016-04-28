# NameParser

Parses a name into various parts including:

* Honorific
* First name
* Middle initial
* Last name 
* Suffix

Ported from [php-name-parser](https://code.google.com/archive/p/php-name-parser/). See also [Splitting names](http://www.onlineaspect.com/2009/08/17/splitting-names/).

PHP has a handy [ucfirst](https://secure.php.net/manual/en/function.ucfirst.php) utility that is used in the original implementation. It turns out a Java implementation has already been discussed on StackOverflow in the article "[How to capitalize the first letter of word in a string using java?](http://stackoverflow.com/a/5725949/6146580)."

The PHP code uses a conditional statement and a regular expression to look for words in *Pascal Case* such as McDonald.

```php
function is_camel_case($word) {
    if (preg_match("|[A-Z]+|s", $word) && preg_match("|[a-z]+|s", $word)) 
        return true;
    return false;
}
```

However, I thought I could get the match in a single regular expression. Sure enough, I quickly found one without having to get my hands dirty with the [Online regex tester and debugger](https://regex101.com/#pcre); see "[RegEx to split camelCase or TitleCase (advanced)](http://stackoverflow.com/a/7599674/6146580)."

## Credits & License

* Based on the [PHP-Name-Parser](http://www.onlineaspect.com/2009/08/17/splitting-names/) by [Josh Fraser](https://github.com/joshfraser).
* Ported to Java by Garve Hays.
* Released under the Apache 2.0 License.
