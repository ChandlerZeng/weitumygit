package com.libtop.weitu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IsbnUtils
{

    private static Pattern pattern = Pattern.compile("\\d[0-9\\-]*[xX0-9]");


    public static String toURL(String isbn)
	{
        try
        {
            int first = Integer.valueOf(isbn.substring(3, 8));
            int second = Integer.valueOf(isbn.substring(8));
            return first / 1024 + "/" + second / 1024 + "/" + isbn + ".jpg";
        }
        catch (Exception e)
        {
            return "no.jpg";
        }
    }


    public static boolean validate(String text)
    {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
        {
            text = text.replaceAll("-", "");
			if (text.length() == 10)
			{
				return validate10(text);
			}
            if ((text.length() == 13) && (text.startsWith("97")))
            {
                return convertIsbn12(text.substring(0, 12)).equals(text);
            }
        }

        return false;
    }


    private static boolean validate10(String text)
    {
        text = text.toUpperCase();
        String isbn = text.substring(0, 9);
        char[] chars = isbn.toCharArray();
        int f = 10;
        int s = 0;
        for (char c : chars)
        {
            int a = valueOf(c);
            s += a * f;
            --f;
        }
        int b = 11 - (s % 11);
		if (b == 10)
		{
			isbn = isbn + "X";
		}
		else if (b == 11)
		{
			isbn = isbn + "0";
		}
		else
		{
			isbn = isbn + b;
		}
        return isbn.equals(text);
    }


    public static String decrypt(String text, Long key)
    {
        long number = (Long.valueOf(unobscure(text)).longValue() - (key.longValue() * 100L)) / 10L;
        return convert(String.valueOf(number));
    }


    public static String encrypt(String isbn, Long key)
    {
        isbn = String.valueOf(Long.valueOf(isbn).longValue() + key.longValue() * 100L);
        return obscure(isbn);
    }


    public static String covertText13ToText10(String isbn)
    {
        isbn = isbn.replaceAll("-", "");
        return covertToText10(isbn);
    }


    public static String covertText10ToText13(String isbn)
    {
        isbn = isbn.replaceAll("-", "");
        return covertToText13(isbn);
    }


    public static String covert13To10(String isbn)
    {
        isbn = isbn.substring(3, 12);
        char[] chars = isbn.toCharArray();
        int f = 10;
        int s = 0;
        for (char c : chars)
        {
            int a = valueOf(c);
            s += a * f;
            --f;
        }
        int b = 11 - (s % 11);
		if (b == 10)
		{
			isbn = isbn + "X";
		}
		else if (b == 11)
		{
			isbn = isbn + "0";
		}
		else
		{
			isbn = isbn + b;
		}
        return isbn;
    }


    public static String covertToText10(String isbn)
    {
        if (isbn.length() == 13)
        {
            isbn = covert13To10(isbn);
        }
        return covertNumberToText(isbn);
    }


    public static String covertToText13(String isbn)
    {
        if (isbn.length() == 10)
        {
            isbn = convert(isbn);
        }
        isbn = isbn.substring(0, 3) + "-" + covertNumberToText(isbn.substring(3));
        return isbn;
    }


    private static String covertNumberToText(String isbn)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = isbn.toCharArray();

        int a = valueOf(chars[0]);
        if (a < 8)
        {
            sb.append(chars[0]);
            sb.append("-");
            int b = valueOf(chars[1]);
            if (b < 1)
            {
                sb.append(chars[1]);
                sb.append(chars[2]);
                sb.append("-");
                sb.append(chars[3]);
                sb.append(chars[4]);
                sb.append(chars[5]);
                sb.append(chars[6]);
                sb.append(chars[7]);
                sb.append(chars[8]);
            }
            else if (b < 5)
            {
                sb.append(chars[1]);
                sb.append(chars[2]);
                sb.append(chars[3]);
                sb.append("-");
                sb.append(chars[4]);
                sb.append(chars[5]);
                sb.append(chars[6]);
                sb.append(chars[7]);
                sb.append(chars[8]);
            }
            else if (b < 8)
            {
                sb.append(chars[1]);
                sb.append(chars[2]);
                sb.append(chars[3]);
                sb.append(chars[4]);
                sb.append("-");
                sb.append(chars[5]);
                sb.append(chars[6]);
                sb.append(chars[7]);
                sb.append(chars[8]);
            }
            else if (b < 9)
            {
                sb.append(chars[1]);
                sb.append(chars[2]);
                sb.append(chars[3]);
                sb.append(chars[4]);
                sb.append(chars[5]);
                sb.append("-");
                sb.append(chars[6]);
                sb.append(chars[7]);
                sb.append(chars[8]);
            }
            else
            {
                int c = valueOf(chars[5]);
                if (c < 9)
                {
                    sb.append(chars[1]);
                    sb.append(chars[2]);
                    sb.append(chars[3]);
                    sb.append(chars[4]);
                    sb.append(chars[5]);
                    sb.append(chars[6]);
                    sb.append("-");
                    sb.append(chars[7]);
                    sb.append(chars[8]);
                }
                else
                {
                    sb.append(chars[1]);
                    sb.append(chars[2]);
                    sb.append(chars[3]);
                    sb.append(chars[4]);
                    sb.append(chars[5]);
                    sb.append(chars[6]);
                    sb.append(chars[7]);
                    sb.append("-");
                    sb.append(chars[8]);
                }
            }
        }
        sb.append("-");
        sb.append(chars[9]);
        return sb.toString();
    }


    public static String toURI(String mixedIsbn)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = mixedIsbn.toCharArray();
        int factor = valueOf(chars[12]) + 1;
        sb.append(valueOf(chars[(valueOf(chars[factor]) + 1)]));
        sb.append("/");
        sb.append(valueOf(chars[(valueOf(chars[(factor + 1)]) + 2)]));
        sb.append("/");
        sb.append(valueOf(chars[(valueOf(chars[(factor + 2)]) + 3)]));
        return sb.toString();
    }


    private static int valueOf(char c)
    {
        return Integer.valueOf(String.valueOf(c)).intValue();
    }


    public static String convert(String isbn)
    {
        isbn = isbn.replaceAll("-", "");
        int length = isbn.length();
		if (length == 13)
		{
			return isbn;
		}
        if (length == 10)
        {
            isbn = isbn.substring(0, 9);
            isbn = "978" + isbn;
        }
        else if (length == 9)
        {
            isbn = "978" + isbn;
        }
        else if (length != 12)
        {
            return null;
        }

        return convertIsbn12(isbn);
    }


    public static String unobscure(String text)
    {
        try
        {
            char secret = text.toCharArray()[12];
            int times = valueOf(secret);
            text = text.substring(0, 12);
            text = unmix(text, times);
            text = unmix2(text);
            text = unmix1(text);
            text = unmix(text);
            text = convertIsbn12(text);
            return text;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public static String obscure(String isbn)
    {
        isbn = mix(isbn);
        isbn = mix1(isbn);
        isbn = mix2(isbn);
        int times = extract(isbn);
        isbn = mix(isbn, times);
        isbn = isbn + String.valueOf(times);
        return isbn;
    }


    private static String mix(String text, int times)
    {
		if (times < 3)
		{
			times += 3;
		}
		else if (times > 7)
		{
			times = 7;
		}
        for (int i = 0; i < times; ++i)
        {
            text = mix(text);
        }
        return text;
    }


    private static String unmix(String text, int times)
    {
		if (times < 3)
		{
			times += 3;
		}
		else if (times > 7)
		{
			times = 7;
		}
        for (int i = 0; i < times; ++i)
        {
            text = unmix(text);
        }
        return text;
    }


    private static String mix1(String text)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < 12; ++i)
        {
            sb.append(chars[(i + 1)]);
            sb.append(chars[i]);
            ++i;
        }
        return sb.toString();
    }


    private static String unmix1(String text)
    {
        return mix1(text);
    }


    private static String mix2(String text)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < 6; ++i)
        {
            sb.append(chars[i]);
            sb.append(chars[(i + 6)]);
        }
        return sb.toString();
    }


    private static String unmix2(String text)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < 12; i += 2)
        {
            sb.append(chars[i]);
        }
        for (int i = 1; i < 12; i += 2)
        {
            sb.append(chars[i]);
        }
        return sb.toString();
    }


    private static String mix(String text)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        int a = chars.length;
        int k = 1;

        for (int i = 5; i >= 0; --i)
        {
            sb.append(chars[(i + k)]);
            sb.append(chars[i]);

            k += 2;
        }
        return sb.toString();
    }


    private static String unmix(String text)
    {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 11; i > 0; i -= 2)
        {
            sb.append(chars[i]);
        }
        for (int i = 0; i < 11; i += 2)
        {
            sb.append(chars[i]);
        }
        return sb.toString();
    }


    private static int extract(String text)
    {
        char[] chars = text.toCharArray();
        return extract(chars);
    }


    private static int extract(char[] chars)
    {
        int a = 0;
        int b = 0;
        for (int i = 0; i < chars.length; ++i)
        {
			if ((i + 1) % 2 == 1)
			{
				a += valueOf(chars[i]);
			}
			else
			{
				b += valueOf(chars[i]);
			}
        }
        int c = 10 - ((a + 3 * b) % 10);
        if (c == 10)
        {
            return 0;
        }
        return c;
    }


    private static String convertIsbn12(String isbn)
    {
        char[] chars = isbn.toCharArray();
        return isbn + extract(chars);
    }

}
