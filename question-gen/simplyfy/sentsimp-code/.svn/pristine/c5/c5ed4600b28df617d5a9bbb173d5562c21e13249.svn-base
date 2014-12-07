using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.IO;

namespace NLPLib{
    // A token, or a word in the sentence or the tree
    public class Token {
        public Token(uint index, string word, string pos) {
            _rep = word;
            _pos = pos;
            _index = index;
        }

        string _rep;
        // The property Text encloses the text representation of the token
        public string Text{
            get{return _rep;}
            set{_rep = value;}
        }

        uint _index;
        // The property Index represents the tokens position in the string
        public uint Index{
            get{ return _index;}
            set{ _index = value;}
        }

        string _pos = null;
        // The POS tag of the word, will be null if no POS tag is assigned.
        public string Pos{
            get { return _pos; }
        }
    }

    public class Sentence {
        private static Regex _pos_tagged_sent = new Regex("([^\\/]+)\\/(\\S+)");
        private static Regex _space_sep = new Regex("\\s+");

        Token[] tokens;

        // Get the token of the sentence
        // Read only property, the sentence won't be changed using this property
        public Token this[int idx]{
            get{return tokens[idx];}
        }

        public int Length {
            get { return tokens==null? 0 : tokens.Length; }
        }

        // Get a sub sentence (a constituent of the sentence)
        // Note the returned sentence will have the same token indices as
        // the original sentence, i.e. it won't start from zero.
        public Sentence this[int start, int end] {
            get {
                Token[] tok = new Token[end - start];
                for (int i = 0; i < end - start; i++) {
                    tok[i] = tokens[i+start];
                }
                return new Sentence(tok);
            }
        }

        private Sentence(Token[] tok) {
            tokens = tok;
        }

        // Constructor, input the string representation of the sentence, and construct the
        // sentence object, this will also parse the POS tag if so provided
        public Sentence(string inp){
            string[] stokens = _space_sep.Split(inp.Trim());
            tokens = new Token[stokens.Length];
            for (uint i = 0; i < stokens.Length; i++){
                Match mat = _pos_tagged_sent.Match(stokens[i]);
                if (mat.Groups.Count == 3) {
                    tokens[i] = new Token((uint)i,mat.Groups[1].Value, mat.Groups[2].Value);
                } else {
                    tokens[i] = new Token((uint)i,stokens[i], null);
                }
            }
        }

        // Format the sentence, you can specify whether the POS tags should be output as well
        public string FormatSentence(bool pos) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tokens.Length; i++) {
                builder.Append(tokens[i].Text);
                if (pos && tokens[i].Pos != null){
                    builder.Append("/");
                    builder.Append(tokens[i].Pos);
                }
                if(i< tokens.Length - 1)
                    builder.Append(" ");
            }
            return builder.ToString();
        }

        public override string ToString() {
            return FormatSentence(true);
        }
    }

    // Representation of syntactic parse tree, note that the subtree of a tree is also
    // a parse tree, no special tree node is presented
    public class SynParseTree {
        Sentence _sentence;
        
        // The actual sentence of the tree
        public Sentence Sentence {
            get { return _sentence; } // Read only getter
        }

        SynParseTree[] _terminals = null;

        public SynParseTree[] Terminals {
            get {
                return _terminals;
            }
        }

        SynParseTree[] _children = null;
        // Return all the children of the parse tree
        public SynParseTree[] Children {
            get{return _children;} // Read only 
        }

        SynParseTree _parent;
        // Return the parent of the parse tree, null if it is the root
        public SynParseTree Parent {
            get { return _parent; } // Read only
        }

        // Whether or not it is a root tree
        public bool IsRoot {
            get { return (_parent == null );}
        }

        // Whether the node is terminal
        public bool IsTerminal {
            get { return (_children == null); }
        }

        int _startIndex, _endIndex;

        // The start/end indices of the constituent, note that the start index is inclusive while 
        // the end index is exclusive
        public int StartIndex {
            get { return (_startIndex); }
        }

        // The start/end indices of the constituent, note that the start index is inclusive while 
        // the end index is exclusive
        public int EndIndex {
            get { return (_endIndex); }
        }

        // Get the constituent of that enclosed by the tree
        public Sentence Constituent {
            get { return _sentence[_startIndex,_endIndex]; } // Not implemented!
        }

        string _tag;

        // The syntactic tag of the tree
        public string Tag {
            get { return _tag; }
        }

        string _term  = null;
        
        public string Terminal {
            get { return _term; }
        }

        enum ParserStatus{
            HEADINGSPACE, HEADINGPAR,
            TAG, ENCLOSEDSPACE, TERMINAL,
            SPACEAFTERTERMINAL, TRAILINGPAR
        }

        private SynParseTree() { }

        // Reconstruct the POS-tagged sentence after parsing
        private void construct_sentence() {
            Stack<SynParseTree> stack = new Stack<SynParseTree>();
            LinkedList<SynParseTree> term_nodes = new LinkedList<SynParseTree>();
            this._startIndex = -2; // Means it is not expanded
            stack.Push(this);
            while (stack.Count > 0) {
                SynParseTree currNode = stack.Peek();
                if (currNode._startIndex == -2) { // Expand it...
                    if (currNode.IsTerminal) {
                        currNode._startIndex = currNode._endIndex = term_nodes.Count;
                        term_nodes.AddFirst(currNode);
                        stack.Pop();
                    } else {
                        foreach(SynParseTree s in currNode.Children){
                            s._startIndex = -2;
                            stack.Push(s);
                        }
                        currNode._startIndex = -1;
                    }
                } else {
                    currNode._startIndex = currNode.Children.First()._startIndex;
                    currNode._endIndex = currNode.Children.Last()._endIndex;
                    stack.Pop();
                }
            }
            StringBuilder bld = new StringBuilder();
            _terminals = new SynParseTree[term_nodes.Count];
            int i = 0;
            foreach(SynParseTree s in term_nodes){
                bld.Append(s.Terminal).Append("/").Append(s.Tag).Append(" ");
                _terminals[i++] = s;
            }
            _sentence = new Sentence(bld.ToString());
        }

        // A helper function for generating error messages
        private string getnearby(int idx, string str) {
            int start = idx - 5;
            if (start < 0) start = 0;
            int end = idx + 5;
            if (end > str.Length) end = str.Length;
            return str.Substring(start, end - start);
        }

        // The function is used to initialize the parse tree by parsing the string, it starts at the position start,
        // and returns the points where a closed bracket is found
        private int parse_string(int start, string str) {
            ParserStatus status = ParserStatus.HEADINGSPACE;
            int tag_start = -1, tag_end = -1, ter_start = -1, ter_end = -1;
            LinkedList<SynParseTree> t_child = new LinkedList<SynParseTree>();
            while (start < str.Length) {
                switch (status) {
                    case ParserStatus.HEADINGSPACE:
                        if (str[start] == ' ' || str[start] == '\t' || str[start] == '\n' || str[start] == '\r' ) start++; // continue with the status
                        else if (str[start] == '(') { status = ParserStatus.HEADINGPAR; start++; } 
                        else throw new TreeFormatException(
                            String.Format("Tree format error, in somewhere around {0}, expecting '(', but get {1}",
                            getnearby(start, str), str[start]));
                        break;
                    case ParserStatus.HEADINGPAR:
                        if (str[start] == ' ' || str[start] == '\t'|| str[start] == '\n' || str[start] == '\r') start++; // continue with the status
                        else if (str[start] == '(') throw new TreeFormatException(
                            String.Format("Tree format error, in somewhere around {0} expecting tag name but get '('", 
                            getnearby(start, str)));
                        else {
                            status = ParserStatus.TAG;
                            tag_start = start;
                            start++;
                        }
                        break;
                    case ParserStatus.TAG:
                        if (str[start] == ' ' || str[start] == '\t' || str[start] == '\n' || str[start] == '\r') {
                            tag_end = start;
                            status = ParserStatus.ENCLOSEDSPACE;
                            start++;
                        } else if (str[start] == '(') {
                            tag_end = start;
                            SynParseTree ntree = new SynParseTree();
                            start = ntree.parse_string(start, str);
                            status = ParserStatus.ENCLOSEDSPACE;
                            t_child.AddLast(ntree);
                        } else if (str[start] == ')')
                            throw new TreeFormatException(
                            String.Format("Tree format error, in somewhere around {0}, expecting tag name, but get {1}",
                            getnearby(start, str), str[start]));
                        else
                            start++;
                        break;
                    case ParserStatus.ENCLOSEDSPACE:
                        if (str[start] == ' ' || str[start] == '\t'|| str[start] == '\n' || str[start] == '\r') { start++; } 
                        else if (str[start] == '(') {
                            SynParseTree ntree = new SynParseTree();
                            start = ntree.parse_string(start, str);
                            t_child.AddLast(ntree);
                        } else if(str[start] == ')') {
                            status = ParserStatus.TRAILINGPAR;
                            start++;
                        } else {
                            ter_start = start;
                            status = ParserStatus.TERMINAL;
                            start++;
                        }
                        break;
                    case ParserStatus.TERMINAL:
                        if (str[start] == ' ' || str[start] == '\t'|| str[start] == '\n' || str[start] == '\r') { 
                            status = ParserStatus.SPACEAFTERTERMINAL;
                            start++;
                        } else if (str[start] == ')') {
                            status = ParserStatus.TRAILINGPAR;
                            ter_end = start;
                            start++;                            
                        } else {
                            start++;
                        }
                        break;
                    case ParserStatus.SPACEAFTERTERMINAL:
                        if (str[start] == ' ' || str[start] == '\t'|| str[start] == '\n' || str[start] == '\r'){
                            start++;
                        }else if(str[start] == ')') {
                            status = ParserStatus.TRAILINGPAR;
                            start++;
                        } else throw new TreeFormatException(
                            String.Format("Tree format error, in somewhere around {0}, expecting ')' or space, but get {1}", 
                            getnearby(start, str), str[start]));
                        break;
                    case ParserStatus.TRAILINGPAR:
                        // Finished parsing
                        if (tag_start < 0 || tag_end < 0) throw new TreeFormatException(
                            String.Format("Tree format error, no tag found!"));
                        _tag = str.Substring(tag_start, tag_end - tag_start).Trim();
                        if (ter_start >= 0 && ter_end > ter_start) {
                            _term = str.Substring(ter_start, ter_end - ter_start).Trim();
                        }
                        if (t_child.Count > 0) {
                            _children = new SynParseTree[t_child.Count];
                            int i = 0;
                            foreach (SynParseTree s in t_child) {
                                s._parent = this;
                                _children[i++] = s;
                            }
                        }
                        return start;
                }
            }
            if (status != ParserStatus.TRAILINGPAR)
                throw new TreeFormatException(
                String.Format("Tree format error, premature ending of input"));
            else {
                if (tag_start < 0 || tag_end < 0) throw new TreeFormatException(
                            String.Format("Tree format error, no tag found!"));
                _tag = str.Substring(tag_start, tag_end - tag_start);
                if (ter_start >= 0 && ter_end > ter_start) {
                    _term = str.Substring(ter_start, ter_end - ter_start);
                }
                if (t_child.Count > 0) {
                    _children = new SynParseTree[t_child.Count];
                    int i = 0;
                    foreach (SynParseTree s in t_child) {
                        s._parent = this;
                        _children[i++] = s;
                    }
                }
                return start;
            }
        }


        public SynParseTree(string inp) {
            parse_string(0,inp.Trim());
            construct_sentence();
        }

        private void to_string(ref StringBuilder bd) {
            bd.Append("(").Append(_tag).Append(" ");
            if (_children != null) {
                foreach (SynParseTree s in _children) {
                    s.to_string(ref bd);
                    bd.Append(" ");
                }
            }
            if (_term != null) {
                bd.Append(_term);
            }
            bd.Append(")");
        }

        public override string ToString() {
            StringBuilder bd = new StringBuilder();
            to_string(ref bd);
            return bd.ToString();
        }
    }

    // A dependency tree, each node is also a tree (and is a terminal)
    public class DepParseTree {
        int _index = -1;
        public int Index {
            get { return _index; }
        }

        string _term = null;
        public string Terminal {
            get { return _term; }
        }

        DepParseTree _parent = null;

        public DepParseTree Parent {
            get { return _parent; }
        }

        string _rel = null;

        public string Relation {
            get {return _rel;}
        }

        DepParseTree[] _children;

        public DepParseTree Children {
            get { return Children; }
        }

        Sentence _sentence = null;

        public Sentence Sentence {
            get { return _sentence; }
        }

        static Regex dep_entry = new Regex("^([^\\(]+)\\((.+)-(\\d+)\\,(.+)\\-(\\d+)\\)$");
        
        public static DepParseTree BuildTree(string input) {
            TextReader reader = new StringReader(input);
            string line;
            
            int maxIndex = 0;
            while ((line = reader.ReadLine()) != null) {
                Match mat = dep_entry.Match(line);
                if (mat != null) {
                    string rel = mat.Groups[1].Value;
                    string parent = mat.Groups[2].Value;
                    string child = mat.Groups[4].Value;
                    int pindex = Convert.ToInt32(mat.Groups[3].Value);
                    int cindex = Convert.ToInt32(mat.Groups[5].Value);
                    if(pindex > maxIndex) maxIndex = pindex;
                    if(cindex > maxIndex) maxIndex = cindex;
                }

            }
            reader.Close();
            reader = new StringReader(input);

            string[] terms = new string[maxIndex];
            DepParseTree[] trees = new DepParseTree[maxIndex];
            int[] parents = new int[maxIndex];
            LinkedList<int>[] ch = new LinkedList<int>[maxIndex];
            for (int i = 0; i < maxIndex; i++) {
                terms[i] = null; parents[i] = -1;
            }

            while ((line = reader.ReadLine()) != null) {
                Match mat = dep_entry.Match(line);
                if (mat != null) {
                    string rel = mat.Groups[1].Value;
                    string parent = mat.Groups[2].Value;
                    string child = mat.Groups[4].Value;
                    int pindex = Convert.ToInt32(mat.Groups[3].Value);
                    int cindex = Convert.ToInt32(mat.Groups[5].Value);
                    if (pindex > maxIndex) maxIndex = pindex;
                    if (cindex > maxIndex) maxIndex = cindex;
                    if (terms[pindex - 1] != null && terms[pindex - 1] != parent) {
                        throw new TreeFormatException(String.Format("Error, mismatching parental entry at index {0}, the old terminal: {1} and the new one {2}",
                            pindex, terms[pindex - 1], parent));
                    } else {
                        terms[pindex - 1] = parent;
                    }
                    if (terms[cindex - 1] != null && terms[cindex - 1] != child) {
                        throw new TreeFormatException(String.Format("Error, mismatching child entry at index {0}, the old terminal: {1} and the new one {2}",
                            cindex, terms[cindex - 1], child));
                    } else {
                        terms[cindex - 1] = child;
                    }
                    if (parents[cindex - 1] > -1 && parents[cindex - 1] != pindex - 1) {
                        throw new TreeFormatException(String.Format("Error, the child node {0} has different parents: {1} and {2}",
                            cindex, parents[cindex - 1] + 1, pindex));
                    } else {
                        parents[cindex - 1] = pindex - 1;
                        if (ch[pindex - 1] == null) {
                            ch[pindex - 1] = new LinkedList<int>();
                        }
                        ch[pindex - 1].AddLast(cindex - 1);
                    }
                }
            }
            StringBuilder bd = new StringBuilder();
            for (int i = 0; i < maxIndex; i++) {

                if (terms[i] == null) {
                    throw new TreeFormatException(String.Format("Error, Missing relations for word at index {0}",
                            i));
                } else {
                    trees[i] = new DepParseTree();
                    trees[i]._term = terms[i];
                    trees[i]._index = i;
                    bd.Append(trees[i].Terminal).Append(" ");
                }
            }
            DepParseTree root = null;

            string sent = bd.ToString();
            Sentence sen = new Sentence(sent);
            for (int i = 0; i < maxIndex; i++) {
                if (parents[i] >= 0) {
                    trees[i]._parent = trees[parents[i]];
                } else {
                    if (root != null) {
                        throw new TreeFormatException(String.Format("Error, More than two roots! {0} and {1}", i , root._index)); 
                    }
                    root = trees[i];
                }
                if (ch[i] != null) {
                    trees[i]._children = new DepParseTree[ch[i].Count];
                    int j = 0;
                    foreach (int v in ch[i]) {
                        trees[i]._children[j++] = trees[v];
                    }
                }
                trees[i]._sentence = sen;
            }

            
            
            return root;
        }
        private DepParseTree() {
            
        }
    }

}
