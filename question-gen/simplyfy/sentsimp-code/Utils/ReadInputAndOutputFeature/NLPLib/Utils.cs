using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;

namespace NLPLib {
    [Serializable]
    public class _ErrorException : Exception{
        public string ErrorMessage{
            get{
                return base.Message.ToString();
            }
        }
    
        public _ErrorException(string errorMessage)
            : base(errorMessage) {}
        public _ErrorException(string errorMessage, Exception innerEx)
            : base(errorMessage, innerEx) {}
    }

    // Thrown when the input format for the parse tree is invalid
    public class TreeFormatException : _ErrorException{
        public TreeFormatException(string errorMessage)
            : base(errorMessage) {}
        public TreeFormatException(string errorMessage, Exception innerEx)
            : base(errorMessage, innerEx) {}
    }

    // Common IO Routines 
    public static class IOUtils {

        /// <summary>
        /// Read an entry that is separated by an empty line.
        /// </summary>
        /// <param name="rd">The text reader to read it</param>
        /// <returns>A string that contain the entry's content, note the linebreaks are preserved. 
        /// null if nothing is read and EOF is encountered</returns>
        public static string ReadEmptyLineSepItem(this TextReader rd) {
            StringBuilder bd = new StringBuilder();
            string line;

            while ((line = rd.ReadLine()) != null) {
                if (line.Trim() == "") {
                    return bd.ToString();
                }
                bd.Append(line).Append(Environment.NewLine);
            }
            string str = bd.ToString();
            if (line == null && str.Length == 0) {
                return null;
            }
            return str;
        }

        /// <summary>
        /// This procedure mimic the python string.strip() function, i.e. removing all the unvisible characters
        /// in the beginning and ending of the sentences.
        /// </summary>
        /// <param name="str">The string object to strip</param>
        /// <returns>Stripped string object</returns>
        public static string Strip(this string str) {
            int start = 0; int end = str.Length-1;
            while (start < str.Length && (Char.IsWhiteSpace(str[start]) || Char.IsControl(str[start]))) {
                start++;
            }
            while (end >= start && (Char.IsControl(str[end]) || Char.IsWhiteSpace(str[end]))) {
                end--;
            }
            int len = end - start + 1;
            if (len > 0) {
                return str.Substring(start, len);
            } else {
                return "";
            }
        }


        private static Hashtable _reg_cache = new Hashtable();
        /// <summary>
        /// Split the sentence using regular expression. It is similar to the java String.Split() function.
        /// Note:
        /// The regex object is cached so no need to compile it again and again.
        /// It is threadsafe, depending on the thread-safety of Hashtable object and the Regex object.
        /// </summary>
        /// <param name="str">The string to split</param>
        /// <param name="pattern">The pattern</param>
        /// <returns>Splitted sentence</returns>
        public static string[] SplitRegex(this string str, string pattern) {
            if (_reg_cache.ContainsKey(pattern)) {
                return ((Regex)_reg_cache[pattern]).Split(str);
            } else {
                Regex re = new Regex(pattern);
                _reg_cache.Add(pattern, re);
                return re.Split(str);
            }
        }
        public static string[] SplitRegex(this string str, string pattern, int count) {
            if (_reg_cache.ContainsKey(pattern)) {
                return ((Regex)_reg_cache[pattern]).Split(str,count);
            } else {
                Regex re = new Regex(pattern);
                _reg_cache.Add(pattern, re);
                return re.Split(str,count);
            }
        }
        public static string[] SplitRegex(this string str, string pattern,int count, int startat) {
            if (_reg_cache.ContainsKey(pattern)) {
                return ((Regex)_reg_cache[pattern]).Split(str,count, startat);
            } else {
                Regex re = new Regex(pattern);
                _reg_cache.Add(pattern, re);
                return re.Split(str,count, startat);
            }
        }

        /// <summary>
        /// Split the string into lines
        /// </summary>
        /// <param name="str"></param>
        /// <returns></returns>
        public static string[] SplitIntoLines(this string str) {
            TextReader rd = new StringReader(str);
            List<string> ls = new List<string>();
            string v;
            while ((v = rd.ReadLine()) != null) {
                ls.Add(v);
            }
            return ls.ToArray();
        }


    }

}
