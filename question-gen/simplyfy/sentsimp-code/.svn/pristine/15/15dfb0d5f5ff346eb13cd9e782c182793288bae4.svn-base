using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NLPLib;


namespace ReadInputAndOutputFeature
{
    class Program
    {
        static void Main(string[] args)
        {
            Sentence s = new Sentence("W1/A W2/BO");
            string oup = s.FormatSentence(false);
            Console.WriteLine(oup);
            
            string str = "sample string";

            Console.WriteLine(Char.IsControl('\n'));    // Output: "True"
            Console.WriteLine(Char.IsControl(str, 7));    // Output: "False"
            Console.ReadKey();
        }
    }
}
