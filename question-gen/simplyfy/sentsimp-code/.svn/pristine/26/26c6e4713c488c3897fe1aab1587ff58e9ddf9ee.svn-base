using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.IO;
using System.Windows.Forms;

using NLPLib;

namespace ShowDemo {

    

    public partial class frmTreeView : Form {
        class TagInfo {
            public int TotalClick { get; set; }
            public override string ToString() {
                return String.Format("{0}", TotalClick);
            }
        }
        private List<SentSimpEntry> _sent_simps = new List<SentSimpEntry>();
        private List<Label> _candidates = new List<Label>();
        private int _selected_sent = -1;
        private bool _selected = false;
        private TextWriter _writer = null;
        private int _last_index = -1;
        private int _saved_index = -1;
        private TextReader _reader = null;

        public frmTreeView() {
            InitializeComponent();
        }

        private void btnTestSentenceIO_Click(object sender, EventArgs e) {
            string inputtxt = txtInput.Text;
            Sentence s = new Sentence(inputtxt);
            txtResult.Text = s.FormatSentence(false);
        }

        private void btnOpenFile_Click(object sender, EventArgs e) {
            DialogResult res = dlgOpenDialog.ShowDialog();
            if (res == DialogResult.OK) {
                string fname = dlgOpenDialog.FileName;
                TextReader reader = new StreamReader(fname);
                string str;
                txtInput.Text = "";
                LinkedList<string> lst = new LinkedList<string>();
                while ((str = reader.ReadLine()) != null) {
                    lst.AddLast(str);
                }
                reader.Close();
                string[] v = new string[lst.Count];
                int i = 0;
                foreach (string l in lst) {
                    v[i++] = l;
                }
                txtInput.Lines = v;
                FileInfo info = new FileInfo("last.file");
                TextWriter wr = new StreamWriter(info.Open(FileMode.Create, FileAccess.Write, FileShare.None));
                wr.Write(txtInput.Text);
                wr.Close();
            }
        }

        private void btnTestParseTreeInput_Click(object sender, EventArgs e) {
            SynParseTree syn = new SynParseTree(txtInput.Text);
            txtResult.Text = syn.ToString() +Environment.NewLine + syn.Sentence.ToString();
        }

        private void btnTestInput_Click(object sender, EventArgs e) {
            TextReader input = new StringReader(txtInput.Text);
            SentSimpEntry ent;
            txtResult.Text = "";
            int i = 0;
            while ((ent = SentSimpEntry.ParseSequentialInput(input,false)) != null) {
                txtResult.Text += ent.GetSVMLightFeature(i++) + Environment.NewLine;
            }
            input.Close();
        }

        private void frmTreeView_Load(object sender, EventArgs e) {
            FileInfo info = new FileInfo("last.file");
            if (info.Exists) {
                TextReader reader = info.OpenText();
                string str;
                txtInput.Text = "";
                LinkedList<string> lst = new LinkedList<string>();
                while ((str = reader.ReadLine()) != null) {
                    lst.AddLast(str);
                }
                string[] v = new string[lst.Count];
                int i = 0;
                foreach (string l in lst) {
                    v[i++] = l;
                }
                txtInput.Lines = v;
                reader.Close();
            }
        }

        private void btnInitRanking_Click(object sender, EventArgs e) {
            TextReader input = new StringReader(txtInput.Text);
            SentSimpEntry ent;
            _sent_simps.Clear();
            while ((ent = SentSimpEntry.ParseSequentialInput(input,true)) != null) {
                _sent_simps.Add(ent);
            }
            update_list();
        }

        private void update_list() {
            lstSentences.Items.Clear();
            foreach (SentSimpEntry ent in _sent_simps) {
                lstSentences.Items.Add(ent.OriginalSentence.FormatSentence(false));
            }
        }

        private void lstSentences_SelectedIndexChanged(object sender, EventArgs e) {
            if (lstSentences.SelectedIndex >= 0) {
                if (_last_index >= 0 && _last_index == lstSentences.SelectedIndex) {
                    return;
                }
                if (_last_index >= 0 && _last_index != lstSentences.SelectedIndex && ! cbNoSave.Checked) {
                    if (MessageBox.Show("You want to save and complete this sentence?", "Save?", MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.No) {
                        lstSentences.SelectedIndex = _last_index;
                        return;
                    }
                    
                    btnSaveRank_Click(sender, e);
                    // Remove the finished one
                    int newIndex = lstSentences.SelectedIndex;
                    if (newIndex > _last_index)
                        newIndex -= 1;
                    lstSentences.Items.RemoveAt(_last_index);
                    _sent_simps.RemoveAt(_last_index);
                    if (_sent_simps.Count == 1) {
                        fill_list();
                        lstSentences.SelectedIndex = 0;
                    } else {
                        lstSentences.SelectedIndex = newIndex;
                    }

                }
                if (_writer != null) {
                    _writer.WriteLine("### BROWSE ### {0}", DateTime.Now);
                }


                _last_index = lstSentences.SelectedIndex;
                _saved_index = -1;
                SentSimpEntry ent = _sent_simps[lstSentences.SelectedIndex];
                txtOriginal.Text = ent.OriginalSentence.FormatSentence(false);
                foreach(Label l in lstRanking.Controls){
                    lstRanking.Controls.Remove(l);
                }
                foreach (Label l in lstSelected.Controls) {
                    lstSelected.Controls.Remove(l);
                }
                lstRanking.Controls.Clear();
                lstSelected.Controls.Clear();
                chkPerfect.Checked = false;
                foreach (SentenceSet ss in ent.SimplifiedSentenceSet) {
                    Label txt = new Label();
                    txt.Parent = lstRanking;
                    txt.Width = lstRanking.ClientRectangle.Width - 10;
                    txt.Left = 0;
                    txt.Font = new Font("Times New Roman", 13);
                    txt.BackColor = Color.Gainsboro;
                    //txt.AutoSize = true;
                    txt.BorderStyle = BorderStyle.Fixed3D;
                    txt.Text = ss.id + Environment.NewLine;
                    string o = "";
                    for (int j = 0; j < txt.Text.Length; j++) {
                        o += " ";
                    }
                    for (int i = 0; i < ss.Count; i++) {
                        txt.Text += o;
                        txt.Text += ss[i].Sentence.FormatSentence(false) + Environment.NewLine;
                    }
                    txt.Height = ((ss.Count+1) * ((int)txt.Font.GetHeight()+3) ) + 1;
                    txt.DoubleClick += AddSentToSelectList;
                    txt.MouseEnter += lstRanking_MouseEnter;
                    txt.MouseClick += ClickOnEntry;
                    txt.Tag = new TagInfo();
                    txt.Show();
                    //txt.Anchor = AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Top;
                    _candidates.Add(txt);
                }
                txtInput.Text = "";
                txtInput.Text = ent.RawText;
            }
            _selected_sent = -1;


        }

        private void lstRanking_Resize(object sender, EventArgs e) {
            foreach (Label l in _candidates) {
                l.Width = lstRanking.ClientRectangle.Width - 10;
            }
        }

        private void FormatInfo() {
            lblInfo.Text = String.Format("Selected {0} sentences", lstSelected.Controls.Count);
        }

        private void RemoveSentFromSelectList(object sender, EventArgs e) {
            if (_selected_sent >= 0) {
                ((Label)lstSelected.Controls[_selected_sent]).BackColor = Color.Aqua;
            }
            Label lbl = (Label)sender;
            lbl.BackColor = Color.Gainsboro;
            //int oldIdx = lstRanking.Controls.IndexOf(lbl);
            //lstRanking.Controls.SetChildIndex(lbl, 0);
            lstSelected.Controls.Remove(lbl);
            lstRanking.Controls.Add(lbl);
            lbl.DoubleClick -= RemoveSentFromSelectList;
            lbl.DoubleClick += AddSentToSelectList;
            lbl.MouseHover -= HoverOnSents;
            lbl.MouseEnter += lstRanking_MouseEnter;
            lbl.MouseEnter -= lstSelected_MouseEnter;
            FormatInfo();
            _selected_sent = -1;
        }

        private void ClickOnEntry(object sender, EventArgs e) {
            Label lbl = (Label)sender;
            TagInfo t = (TagInfo)lbl.Tag;
            t.TotalClick += 1;
            FormatInfo();
        }


        private void AddSentToSelectList(object sender, EventArgs e) {
            Label lbl = (Label)sender;
            lbl.BackColor = Color.Aqua;
            //int oldIdx = lstRanking.Controls.IndexOf(lbl);
            //lstRanking.Controls.SetChildIndex(lbl, 0);
            lstRanking.Controls.Remove(lbl);
            lstSelected.Controls.Add(lbl);
            lbl.DoubleClick += RemoveSentFromSelectList;
            lbl.DoubleClick -= AddSentToSelectList;
            lbl.MouseHover += HoverOnSents;
            lbl.MouseClick += SelectOnSelected;
            lbl.MouseEnter -= lstRanking_MouseEnter;
            lbl.MouseEnter += lstSelected_MouseEnter;
            FormatInfo();
        }

     
        private void lstSelected_Resize(object sender, EventArgs e) {
            foreach (Label l in _candidates) {
                l.Width = lstSelected.ClientRectangle.Width - 10;
            }
        }


        private void HoverOnSents(object sender, EventArgs e) {
            Label lbl = (Label)(sender);
            int index = lstSelected.Controls.IndexOf(lbl) + 1;
            tipSentenceOrder.Show(String.Format("Order : {0}",index), lbl);
        }

        private void SelectOnSelected(object sender, EventArgs e) {
            Label lbl = (Label)(sender);
            int index = lstSelected.Controls.IndexOf(lbl);
            if (index >= 0) {
                if (_selected && _selected_sent == index - 1 && _selected_sent >= 0) {
                    ((Label)lstSelected.Controls[_selected_sent]).BackColor = Color.Aqua;
                    _selected_sent = index;
                    lbl.BackColor = Color.GreenYellow;
                    _selected = false;
                }  else if (_selected_sent == index) {
                    ((Label)lstSelected.Controls[_selected_sent]).BackColor = Color.Aqua;
                    _selected_sent = -1;
                    _selected = false;
                } else if(_selected_sent >= 0) {
                    lstSelected.Controls.SetChildIndex(lstSelected.Controls[_selected_sent], index);
                    _selected_sent = index;
                    _selected = true;
                    //lbl.BackColor = Color.GreenYellow;
                } else {
                    lbl.BackColor = Color.GreenYellow;
                    _selected_sent = index;
                    _selected = false;
                }
            }
            
        }



        private void btnSaveRank_Click(object sender, EventArgs e) {
            if (_writer == null) {

                if (dlgSave.ShowDialog() == DialogResult.OK) {
                    FileInfo f = new FileInfo(dlgSave.FileName);
                    if (f.Exists) {
                        _writer = new StreamWriter(f.Open(FileMode.Append,FileAccess.Write, FileShare.Read));
                    } else {
                        _writer = new StreamWriter(f.Open(FileMode.CreateNew, FileAccess.Write, FileShare.Read));
                    }
                }
            }

            if (_writer != null && _saved_index != _last_index) {
                _writer.WriteLine(String.Format("ORI ||| {0} ||| {1}", chkPerfect.Checked, txtOriginal.Text));
                int i = 0;
                foreach(Label l in lstSelected.Controls){
                    string t = l.Text.Strip();
                    TagInfo ti = (TagInfo)l.Tag;
                    string[] tx = t.SplitIntoLines();
                    _writer.Write(String.Format("+ {0} ||| ", i++));
                    for (int j = 0; j < tx.Length; j++ ) {
                        _writer.Write(tx[j].Trim());
                        if (j < tx.Length - 1)
                            _writer.Write(" ||| ");
                        else
                            _writer.WriteLine(" ||| {0}", ti.ToString().Trim());
                    }
                    
                }

                foreach (Label l in lstRanking.Controls) {
                    string t = l.Text.Strip();
                    TagInfo ti = (TagInfo)l.Tag;
                    string[] tx = t.SplitIntoLines();
                    _writer.Write(String.Format("- {0} ||| ", i++));
                    for (int j = 0; j < tx.Length; j++) {
                        _writer.Write(tx[j].Trim());
                        if (j < tx.Length - 1)
                            _writer.Write(" ||| ");
                        else
                            _writer.WriteLine(" ||| {0}", ti);
                    }

                }

                _writer.Flush();
                _writer.WriteLine();
                _saved_index = _last_index;
            }
            
        }

        private void frmTreeView_FormClosing(object sender, FormClosingEventArgs e) {
            if (_last_index != -1) {
                btnSaveRank_Click(sender, e);
            }
        }

        private void fill_list() {
            SentSimpEntry ent;
            while ((ent = SentSimpEntry.ParseSequentialInput(_reader,true)) != null && _sent_simps.Count <= 10) {
                _sent_simps.Add(ent);
            }
            update_list();
        }

        private void btnOpenStream_Click(object sender, EventArgs e) {
            DialogResult res = dlgOpenDialog.ShowDialog();
            if (res == DialogResult.OK) {
                string fname = dlgOpenDialog.FileName;
                _reader = new StreamReader(fname);
                _sent_simps.Clear();
                fill_list();
                if (_writer != null) {
                    _writer.Close();
                    _writer = null;
                }
                string nfname = fname + ".label";
                FileInfo fi = new FileInfo(nfname);
                if (fi.Exists) {
                    _writer = new StreamWriter(fi.Open(FileMode.Append, FileAccess.Write, FileShare.Read));
                } else {
                    _writer = new StreamWriter(fi.Open(FileMode.CreateNew, FileAccess.Write, FileShare.Read));
                }
                //_writer = new StreamWriter(fname + ".label");
                tabOutput.SelectedIndex = 1;
            }
            

        }

        private void btnSplit_Click(object sender, EventArgs e) {
            DialogResult res = dlgOpenDialog.ShowDialog();
            int part = 0;
            int count = 0;
            TextWriter writer = null;
            if (res == DialogResult.OK) {
                string fname = dlgOpenDialog.FileName;
                TextReader reader = new StreamReader(fname);
                SentSimpEntry sent = new SentSimpEntry();
                string entry;
                while ((entry = reader.ReadLine()) != null) {
                    if (entry.StartsWith("#END#")) {                        
                        writer.WriteLine(entry);                        
                        count++;
                        if (count >= 10) {
                            writer.Close();
                            writer = null;
                            entry = reader.ReadLine();
                            count = 0;
                            part++;
                        }
                        continue;
                    }
                    if (writer == null) {
                        //entry = reader.ReadLine();
                        writer = new StreamWriter(String.Format("{0}.part.{1:0000}", fname, part));
                    }
                    if (entry.StartsWith("0 ||| ")) {
                        continue;
                    }
                    writer.WriteLine(entry);
                }

                if (writer!=null) {
                    writer.Close();
                }
            }

        }

        private void lstRanking_MouseEnter(object sender, EventArgs e) {
            lstRanking.Focus();
            
        }

        private void lstRanking_Paint(object sender, PaintEventArgs e) {

        }

        private void lstSelected_MouseEnter(object sender, EventArgs e) {
            lstSelected.Focus();
        }

        private void btnSplit_Click_1(object sender, EventArgs e) {
            DialogResult res = dlgOpenDialog.ShowDialog();
            int part = 0;
            int count = 0;
            TextWriter writer = null;
            if (res == DialogResult.OK) {
                string fname = dlgOpenDialog.FileName;
                TextReader reader = new StreamReader(fname);
                SentSimpEntry sent = new SentSimpEntry();
                string entry;
                while ((entry = reader.ReadLine()) != null) {
                    if (entry.StartsWith("#END#")) {
                        writer.WriteLine(entry);
                        count++;
                        if (count >= 10) {
                            writer.Close();
                            writer = null;
                            count = 0;
                            part++;
                        }
                        continue;
                    }
                    if (writer == null) {
                        writer = new StreamWriter(String.Format("{0}.part.{1:0000}", fname, part));
                    }
                    if (entry.StartsWith("0 ||| ")) {
                        continue;
                    }
                    writer.WriteLine(entry);
                }

                if (writer != null) {
                    writer.Close();
                }
            }
        }

        private void lstRanking_PreviewKeyDown(object sender, PreviewKeyDownEventArgs e) {
            if (e.KeyCode == Keys.PageDown) {
                //MessageBox.Show("Page Down");
                bool last = false;
                foreach (Label l in lstRanking.Controls) {
                    if (l.Top > lstRanking.ClientSize.Height*2) {
                        lstRanking.ScrollControlIntoView(l);
                        last = true;
                        break;
                    }
                }
                if (!last) {
                    lstRanking.ScrollControlIntoView(lstRanking.Controls[lstRanking.Controls.Count - 1]);
                }
            } else if (e.KeyCode == Keys.PageUp) {
                foreach (Label l in lstRanking.Controls) {
                    if (l.Bottom > 0 -  lstRanking.ClientSize.Height) {
                        lstRanking.ScrollControlIntoView(l);
                        break;
                    }
                }
            }
        }

        private void frmTreeView_Resize(object sender, EventArgs e) {
            leftFlowControl.Width = btnSplit.Width + 10;
        }

        private void mainSplitContainer_DoubleClick(object sender, EventArgs e) {
            mainSplitContainer.Panel1Collapsed = !mainSplitContainer.Panel1Collapsed;
        }

        private void mainSplitContainer_Panel2_Paint(object sender, PaintEventArgs e) {

        }

        private void btnhs_Click(object sender, EventArgs e) {
            mainSplitContainer.Panel1Collapsed = !mainSplitContainer.Panel1Collapsed;
            btnhs.Text = mainSplitContainer.Panel1Collapsed ? ">" : "<";
        }

        private void frmTreeView_KeyDown(object sender, KeyEventArgs e) {
            if (e.KeyCode == Keys.C) {
                chkPerfect.Checked = !chkPerfect.Checked;
            }
        }

        private void txtResult_TextChanged(object sender, EventArgs e) {

        }

    }
}
