using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;

namespace NLPLib {
    /************************************************************************/
    /*  Sentence Simplification Entries                                     */
    /************************************************************************/
  
	public class SentenceWithFeatures{
		Sentence _sentence;
		public Sentence Sentence{
			get { return _sentence;}
		}
		
		float[] _features;
		
		public float[] Features{
			get {return _features;}
			set {_features = value;}
		}
		
		public SentenceWithFeatures(Sentence sent, float[] features){
			_sentence = sent;
			_features = features;
		}

        bool _valid = false;

        public bool IsValid {
            get { return _valid; }
            set { _valid = value;  }
        }

        int[] _map_to_oidx;

        public int OriginalIndex(int i) {
            return _map_to_oidx[i];
        }

        int[] _phrase_boundaries;

        public int PhraseBoundaries(int i) {
            return _phrase_boundaries[i];
        }

        public int PhraseCount(int i) {
            return _phrase_boundaries.Count();
        }

        public SentenceWithFeatures(string inp) {
            List<string> words = new List<string>();
            string line = inp;
            string[] fields = line.Trim().SplitRegex("\\|\\|\\|");
            _valid = (Convert.ToInt32(fields[0]) == 1);
            int pcount = fields.Length - 2;
            _phrase_boundaries = new int[pcount];
            for (int i = 0; i < pcount; i++) {
                string[] wds = fields[i+1].Trim().SplitRegex("\\s+");
                foreach(string w in wds)  words.Add(w.Trim());
                _phrase_boundaries[i]= words.Count;
            }
            _map_to_oidx = new int[words.Count];
            StringBuilder sb = new StringBuilder();
            _map_to_oidx = new int[words.Count];
            int j = 0;
            foreach(string w in words){
                int p = w.LastIndexOf('-');
                string wd = w.Substring(0,p);
                int idx = Convert.ToInt32(w.Substring(p+1));
                _map_to_oidx[j++] = idx -1;
                sb.Append(wd).Append(" ");
            }
            _sentence = new Sentence(sb.ToString().Trim());
            string[] feats = fields[fields.Length-1].Split(',');
            List<float> fe = new List<float>();
            foreach(string f in feats){                
                float fl =0;
                bool b = float.TryParse(f, out fl);
                if(b)
                    fe.Add(fl);
                
            }
            _features = fe.ToArray();
        }
	}
	
	public class SentenceSet{
		List<SentenceWithFeatures> _sentence = new List<SentenceWithFeatures>();
		
		public int Count{
			get {return _sentence.Count;}
		}

        public string id {get; set; }
		
		public SentenceWithFeatures this[int idx]{
			get {return _sentence.ElementAt(idx);}
			set {_sentence.RemoveAt(idx);_sentence.Insert(idx,value);}
		}
		
		float[] _features;
		public float[] Features{
			get {return _features;}
			set {_features = value;}
		}
		
		public void Add(SentenceWithFeatures sent){
			_sentence.Add(sent);
		}
		
		public void RemoveAt(int idx){
			_sentence.RemoveAt(idx);
		}
	}
	
    public class SentSimpEntry {
		Sentence _original_sent;
		
		public Sentence OriginalSentence{
			get{return _original_sent;}
			set {_original_sent = value;}
		}

        SynParseTree _syn_tree;

        public SynParseTree SyntaxParseTree {
            get {return _syn_tree;}
            set {_syn_tree = value;}
        }

        DepParseTree _dep_tree;

        public DepParseTree DependencyParseTree {
            get {return _dep_tree;}
            set {_dep_tree = value;}
        }
		
		Hashtable _simp_sents = new Hashtable();
		
		public Hashtable SimplifiedSentences{
			get {return _simp_sents;}
			set {_simp_sents = value;}
		}

        string _raw;

        public string RawText {
            get {
                return _raw;
            }
            set {
                StringReader rd = new StringReader(value);
                StringBuilder bd = new StringBuilder();
                string s;
                while ((s = rd.ReadLine()) != null) {
                    bd.Append(s).Append(Environment.NewLine);
                }
                _raw = bd.ToString();
            }
        }
		
		List<SentenceSet> _sent_sets = new List<SentenceSet>();
		
		public List<SentenceSet> SimplifiedSentenceSet {
			get {return _sent_sets;}
            set { _sent_sets = value; }
		}

        private SentenceSet  parse_sent_set(string sent) {
            string[] lines = sent.SplitIntoLines();
            if (lines[0].Split(' ')[0] != "HYP") {
                return null;
            }
            SentenceSet s = new SentenceSet();
            for (int i = 1; i < lines.Length; i++) {
                SentenceWithFeatures fe = new SentenceWithFeatures(lines[i]);
                if (!_simp_sents.ContainsKey(fe.Sentence.FormatSentence(false))) {
                    return null;
                } else {
                    fe = (SentenceWithFeatures)_simp_sents[fe.Sentence.FormatSentence(false)];
                    s.Add(fe);
                }
            }
            return s;

        }

        public static SentSimpEntry ParseSequentialInput(TextReader reader, bool skip_tree) {
            SentSimpEntry sent = new SentSimpEntry();
            StringBuilder bd = new StringBuilder();
            string entry;
            if ((entry = reader.ReadEmptyLineSepItem()) == null) return null;
            bd.Append(entry).Append(Environment.NewLine);
            while (entry.StartsWith("#END#") || entry.StartsWith("HYP ")) {
                if ((entry = reader.ReadEmptyLineSepItem()) == null) return null;
                bd.Append(entry).Append(Environment.NewLine);
            }
                
            sent._original_sent = new Sentence(entry);
            if ((entry = reader.ReadEmptyLineSepItem()) == null) return null;
            bd.Append(entry).Append(Environment.NewLine);
            if(!skip_tree) sent._syn_tree = new SynParseTree(entry);
            if ((entry = reader.ReadEmptyLineSepItem()) == null) return null;
            bd.Append(entry).Append(Environment.NewLine);
            if (!skip_tree) sent._dep_tree = DepParseTree.BuildTree(entry);
            if ((entry = reader.ReadEmptyLineSepItem()) == null) return null;
            bd.Append(entry).Append(Environment.NewLine);
            TextReader areader = new StringReader(entry);
            string el;
            while ((el = areader.ReadLine() )!= null) {
                SentenceWithFeatures swf = new SentenceWithFeatures(el);
                string key = swf.Sentence.FormatSentence(false);
                if (sent._simp_sents.ContainsKey(key)) {
                    swf = (SentenceWithFeatures)sent._simp_sents[key];
                } else {
                    sent._simp_sents.Add(key, swf);
                }
            }
            int j = 0;
            while ((entry = reader.ReadEmptyLineSepItem()) != null) {
                bd.Append(entry).Append(Environment.NewLine);
                if (entry.StartsWith("1-LINE-HYP:"))
                    continue;
                entry = entry.Strip();
                if (entry == "#END#") break;
                SentenceSet s = sent.parse_sent_set(entry);
                s.id = String.Format("HYP {0}",j++);
                if (s != null) {
                    sent._sent_sets.Add(s);
                }
            }
            sent.RawText = bd.ToString();
            return sent;
        }

        /// <summary>
        /// Get the SVM light feature vector
        /// </summary>
        /// <param name="baseidx"></param>
        /// <returns></returns>
        public string GetSVMLightFeature(int grpidx) {
            if(_sent_sets.Count==0)
                return "";
            StringBuilder bd = new StringBuilder();
            int nfl = _sent_sets[0][0].Features.Length;
            float[] feats = new float[_sent_sets[0][0].Features.Length * 3];
            int sid = 0;
            foreach (SentenceSet ss in _sent_sets) {
                // Three kinds of features, max, average and min
                for (int j = 0; j < ss[0].Features.Length; j++) {
                    feats[j] = feats[nfl+j] = feats[nfl*2 + j ] = ss[0].Features[j];
                }
                for (int i = 1; i < ss.Count; i++) {
                    for(int j = 0; j< ss[i].Features.Length ; j++){
                        feats[j] = feats[j] > ss[i].Features[j] ? feats[j] : ss[i].Features[j];
                        feats[j+nfl] = feats[j+nfl] < ss[i].Features[j] ? feats[j+nfl] : ss[i].Features[j];
                        feats[j + 2 * nfl] += ss[i].Features[j];
                    }
                }
                for (int j = 0; j < ss[0].Features.Length; j++) {
                    feats[nfl * 2 + j] /= ss[0].Features.Length;
                }
                bd.Append(String.Format("{0} {1} ", grpidx, sid++));
                for (int j = 0; j < ss[0].Features.Length; j++) {
                    bd.Append(String.Format("{0} {1} {2}", feats[j], feats[j + nfl], feats[j + nfl * 2]));
                }
                bd.Append("\n");
            }
            return bd.ToString();
        }
    }
}