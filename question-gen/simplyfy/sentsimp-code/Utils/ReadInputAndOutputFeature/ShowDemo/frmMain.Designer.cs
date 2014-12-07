namespace ShowDemo {
    partial class frmTreeView {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing) {
            if (disposing && (components != null)) {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent() {
            this.components = new System.ComponentModel.Container();
            this.mainSplitContainer = new System.Windows.Forms.SplitContainer();
            this.leftFlowControl = new System.Windows.Forms.FlowLayoutPanel();
            this.btnTestSentenceIO = new System.Windows.Forms.Button();
            this.btnOpenFile = new System.Windows.Forms.Button();
            this.btnTestParseTreeInput = new System.Windows.Forms.Button();
            this.btnTestInput = new System.Windows.Forms.Button();
            this.btnOpenStream = new System.Windows.Forms.Button();
            this.btnInitRanking = new System.Windows.Forms.Button();
            this.btnSaveRank = new System.Windows.Forms.Button();
            this.btnSplit = new System.Windows.Forms.Button();
            this.cbNoSave = new System.Windows.Forms.CheckBox();
            this.btnhs = new System.Windows.Forms.Button();
            this.rightSplitContainer = new System.Windows.Forms.SplitContainer();
            this.label1 = new System.Windows.Forms.Label();
            this.txtInput = new System.Windows.Forms.TextBox();
            this.tabOutput = new System.Windows.Forms.TabControl();
            this.txtOutput = new System.Windows.Forms.TabPage();
            this.txtResult = new System.Windows.Forms.TextBox();
            this.tblRank = new System.Windows.Forms.TabPage();
            this.chkPerfect = new System.Windows.Forms.CheckBox();
            this.lblInfo = new System.Windows.Forms.Label();
            this.splVis = new System.Windows.Forms.SplitContainer();
            this.lstSelected = new System.Windows.Forms.FlowLayoutPanel();
            this.lstRanking = new System.Windows.Forms.FlowLayoutPanel();
            this.lblOrigin = new System.Windows.Forms.Label();
            this.txtOriginal = new System.Windows.Forms.RichTextBox();
            this.lblSent = new System.Windows.Forms.Label();
            this.lstSentences = new System.Windows.Forms.ListBox();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.dlgOpenDialog = new System.Windows.Forms.OpenFileDialog();
            this.tipSentenceOrder = new System.Windows.Forms.ToolTip(this.components);
            this.dlgSave = new System.Windows.Forms.SaveFileDialog();
            ((System.ComponentModel.ISupportInitialize)(this.mainSplitContainer)).BeginInit();
            this.mainSplitContainer.Panel1.SuspendLayout();
            this.mainSplitContainer.Panel2.SuspendLayout();
            this.mainSplitContainer.SuspendLayout();
            this.leftFlowControl.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.rightSplitContainer)).BeginInit();
            this.rightSplitContainer.Panel1.SuspendLayout();
            this.rightSplitContainer.Panel2.SuspendLayout();
            this.rightSplitContainer.SuspendLayout();
            this.tabOutput.SuspendLayout();
            this.txtOutput.SuspendLayout();
            this.tblRank.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.splVis)).BeginInit();
            this.splVis.Panel1.SuspendLayout();
            this.splVis.Panel2.SuspendLayout();
            this.splVis.SuspendLayout();
            this.SuspendLayout();
            // 
            // mainSplitContainer
            // 
            this.mainSplitContainer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.mainSplitContainer.Location = new System.Drawing.Point(0, 0);
            this.mainSplitContainer.Margin = new System.Windows.Forms.Padding(2);
            this.mainSplitContainer.Name = "mainSplitContainer";
            // 
            // mainSplitContainer.Panel1
            // 
            this.mainSplitContainer.Panel1.Controls.Add(this.leftFlowControl);
            // 
            // mainSplitContainer.Panel2
            // 
            this.mainSplitContainer.Panel2.Controls.Add(this.btnhs);
            this.mainSplitContainer.Panel2.Controls.Add(this.rightSplitContainer);
            this.mainSplitContainer.Panel2.Paint += new System.Windows.Forms.PaintEventHandler(this.mainSplitContainer_Panel2_Paint);
            this.mainSplitContainer.Size = new System.Drawing.Size(1116, 754);
            this.mainSplitContainer.SplitterDistance = 108;
            this.mainSplitContainer.SplitterWidth = 3;
            this.mainSplitContainer.TabIndex = 0;
            this.mainSplitContainer.DoubleClick += new System.EventHandler(this.mainSplitContainer_DoubleClick);
            // 
            // leftFlowControl
            // 
            this.leftFlowControl.Controls.Add(this.btnTestSentenceIO);
            this.leftFlowControl.Controls.Add(this.btnOpenFile);
            this.leftFlowControl.Controls.Add(this.btnTestParseTreeInput);
            this.leftFlowControl.Controls.Add(this.btnTestInput);
            this.leftFlowControl.Controls.Add(this.btnOpenStream);
            this.leftFlowControl.Controls.Add(this.btnInitRanking);
            this.leftFlowControl.Controls.Add(this.btnSaveRank);
            this.leftFlowControl.Controls.Add(this.btnSplit);
            this.leftFlowControl.Controls.Add(this.cbNoSave);
            this.leftFlowControl.Dock = System.Windows.Forms.DockStyle.Fill;
            this.leftFlowControl.Location = new System.Drawing.Point(0, 0);
            this.leftFlowControl.Margin = new System.Windows.Forms.Padding(2);
            this.leftFlowControl.Name = "leftFlowControl";
            this.leftFlowControl.Size = new System.Drawing.Size(108, 754);
            this.leftFlowControl.TabIndex = 0;
            // 
            // btnTestSentenceIO
            // 
            this.btnTestSentenceIO.AutoEllipsis = true;
            this.btnTestSentenceIO.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnTestSentenceIO.Location = new System.Drawing.Point(2, 2);
            this.btnTestSentenceIO.Margin = new System.Windows.Forms.Padding(2);
            this.btnTestSentenceIO.Name = "btnTestSentenceIO";
            this.btnTestSentenceIO.Size = new System.Drawing.Size(96, 45);
            this.btnTestSentenceIO.TabIndex = 0;
            this.btnTestSentenceIO.Text = "Test Sentence Input";
            this.btnTestSentenceIO.UseVisualStyleBackColor = true;
            this.btnTestSentenceIO.Click += new System.EventHandler(this.btnTestSentenceIO_Click);
            // 
            // btnOpenFile
            // 
            this.btnOpenFile.AutoEllipsis = true;
            this.btnOpenFile.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnOpenFile.Location = new System.Drawing.Point(2, 51);
            this.btnOpenFile.Margin = new System.Windows.Forms.Padding(2);
            this.btnOpenFile.Name = "btnOpenFile";
            this.btnOpenFile.Size = new System.Drawing.Size(96, 45);
            this.btnOpenFile.TabIndex = 1;
            this.btnOpenFile.Text = "Open Input File (Full)";
            this.btnOpenFile.UseVisualStyleBackColor = true;
            this.btnOpenFile.Click += new System.EventHandler(this.btnOpenFile_Click);
            // 
            // btnTestParseTreeInput
            // 
            this.btnTestParseTreeInput.AutoEllipsis = true;
            this.btnTestParseTreeInput.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnTestParseTreeInput.Location = new System.Drawing.Point(2, 100);
            this.btnTestParseTreeInput.Margin = new System.Windows.Forms.Padding(2);
            this.btnTestParseTreeInput.Name = "btnTestParseTreeInput";
            this.btnTestParseTreeInput.Size = new System.Drawing.Size(96, 45);
            this.btnTestParseTreeInput.TabIndex = 2;
            this.btnTestParseTreeInput.Text = "Test SynParse Input";
            this.btnTestParseTreeInput.UseVisualStyleBackColor = true;
            this.btnTestParseTreeInput.Click += new System.EventHandler(this.btnTestParseTreeInput_Click);
            // 
            // btnTestInput
            // 
            this.btnTestInput.AutoEllipsis = true;
            this.btnTestInput.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnTestInput.Location = new System.Drawing.Point(2, 149);
            this.btnTestInput.Margin = new System.Windows.Forms.Padding(2);
            this.btnTestInput.Name = "btnTestInput";
            this.btnTestInput.Size = new System.Drawing.Size(96, 45);
            this.btnTestInput.TabIndex = 3;
            this.btnTestInput.Text = "Test Full Input";
            this.btnTestInput.UseVisualStyleBackColor = true;
            this.btnTestInput.Click += new System.EventHandler(this.btnTestInput_Click);
            // 
            // btnOpenStream
            // 
            this.btnOpenStream.AutoEllipsis = true;
            this.btnOpenStream.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnOpenStream.Location = new System.Drawing.Point(2, 198);
            this.btnOpenStream.Margin = new System.Windows.Forms.Padding(2);
            this.btnOpenStream.Name = "btnOpenStream";
            this.btnOpenStream.Size = new System.Drawing.Size(96, 45);
            this.btnOpenStream.TabIndex = 6;
            this.btnOpenStream.Text = "Open Input File (Stream)";
            this.btnOpenStream.UseVisualStyleBackColor = true;
            this.btnOpenStream.Click += new System.EventHandler(this.btnOpenStream_Click);
            // 
            // btnInitRanking
            // 
            this.btnInitRanking.AutoEllipsis = true;
            this.btnInitRanking.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnInitRanking.Location = new System.Drawing.Point(2, 247);
            this.btnInitRanking.Margin = new System.Windows.Forms.Padding(2);
            this.btnInitRanking.Name = "btnInitRanking";
            this.btnInitRanking.Size = new System.Drawing.Size(96, 45);
            this.btnInitRanking.TabIndex = 4;
            this.btnInitRanking.Text = "Initialize Ranking";
            this.btnInitRanking.UseVisualStyleBackColor = true;
            this.btnInitRanking.Click += new System.EventHandler(this.btnInitRanking_Click);
            // 
            // btnSaveRank
            // 
            this.btnSaveRank.AutoEllipsis = true;
            this.btnSaveRank.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnSaveRank.Location = new System.Drawing.Point(2, 296);
            this.btnSaveRank.Margin = new System.Windows.Forms.Padding(2);
            this.btnSaveRank.Name = "btnSaveRank";
            this.btnSaveRank.Size = new System.Drawing.Size(96, 45);
            this.btnSaveRank.TabIndex = 5;
            this.btnSaveRank.Text = "Save Ranking";
            this.btnSaveRank.UseVisualStyleBackColor = true;
            this.btnSaveRank.Click += new System.EventHandler(this.btnSaveRank_Click);
            // 
            // btnSplit
            // 
            this.btnSplit.AutoEllipsis = true;
            this.btnSplit.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnSplit.Location = new System.Drawing.Point(2, 345);
            this.btnSplit.Margin = new System.Windows.Forms.Padding(2);
            this.btnSplit.Name = "btnSplit";
            this.btnSplit.Size = new System.Drawing.Size(96, 45);
            this.btnSplit.TabIndex = 7;
            this.btnSplit.Text = "Split Into Segments";
            this.btnSplit.UseVisualStyleBackColor = true;
            this.btnSplit.Click += new System.EventHandler(this.btnSplit_Click);
            // 
            // cbNoSave
            // 
            this.cbNoSave.AutoSize = true;
            this.cbNoSave.Location = new System.Drawing.Point(3, 395);
            this.cbNoSave.Name = "cbNoSave";
            this.cbNoSave.Size = new System.Drawing.Size(85, 17);
            this.cbNoSave.TabIndex = 8;
            this.cbNoSave.Text = "Browse Only";
            this.cbNoSave.UseVisualStyleBackColor = true;
            // 
            // btnhs
            // 
            this.btnhs.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)));
            this.btnhs.Location = new System.Drawing.Point(3, 0);
            this.btnhs.Name = "btnhs";
            this.btnhs.Size = new System.Drawing.Size(12, 729);
            this.btnhs.TabIndex = 1;
            this.btnhs.Text = "<";
            this.btnhs.UseVisualStyleBackColor = true;
            this.btnhs.Click += new System.EventHandler(this.btnhs_Click);
            // 
            // rightSplitContainer
            // 
            this.rightSplitContainer.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.rightSplitContainer.Location = new System.Drawing.Point(14, 0);
            this.rightSplitContainer.Margin = new System.Windows.Forms.Padding(2);
            this.rightSplitContainer.Name = "rightSplitContainer";
            this.rightSplitContainer.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // rightSplitContainer.Panel1
            // 
            this.rightSplitContainer.Panel1.Controls.Add(this.label1);
            this.rightSplitContainer.Panel1.Controls.Add(this.txtInput);
            // 
            // rightSplitContainer.Panel2
            // 
            this.rightSplitContainer.Panel2.Controls.Add(this.tabOutput);
            this.rightSplitContainer.Size = new System.Drawing.Size(995, 730);
            this.rightSplitContainer.SplitterDistance = 155;
            this.rightSplitContainer.SplitterWidth = 3;
            this.rightSplitContainer.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.label1.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label1.Location = new System.Drawing.Point(2, 0);
            this.label1.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(917, 26);
            this.label1.TabIndex = 1;
            this.label1.Text = "Input:";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // txtInput
            // 
            this.txtInput.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.txtInput.Font = new System.Drawing.Font("Consolas", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtInput.Location = new System.Drawing.Point(2, 28);
            this.txtInput.Margin = new System.Windows.Forms.Padding(2);
            this.txtInput.Multiline = true;
            this.txtInput.Name = "txtInput";
            this.txtInput.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.txtInput.Size = new System.Drawing.Size(989, 125);
            this.txtInput.TabIndex = 0;
            // 
            // tabOutput
            // 
            this.tabOutput.Appearance = System.Windows.Forms.TabAppearance.FlatButtons;
            this.tabOutput.Controls.Add(this.txtOutput);
            this.tabOutput.Controls.Add(this.tblRank);
            this.tabOutput.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabOutput.Location = new System.Drawing.Point(0, 0);
            this.tabOutput.Margin = new System.Windows.Forms.Padding(2);
            this.tabOutput.Name = "tabOutput";
            this.tabOutput.SelectedIndex = 0;
            this.tabOutput.Size = new System.Drawing.Size(995, 572);
            this.tabOutput.TabIndex = 1;
            // 
            // txtOutput
            // 
            this.txtOutput.Controls.Add(this.txtResult);
            this.txtOutput.Location = new System.Drawing.Point(4, 25);
            this.txtOutput.Margin = new System.Windows.Forms.Padding(2);
            this.txtOutput.Name = "txtOutput";
            this.txtOutput.Padding = new System.Windows.Forms.Padding(2);
            this.txtOutput.Size = new System.Drawing.Size(987, 543);
            this.txtOutput.TabIndex = 0;
            this.txtOutput.Text = "Output";
            this.txtOutput.UseVisualStyleBackColor = true;
            // 
            // txtResult
            // 
            this.txtResult.Dock = System.Windows.Forms.DockStyle.Fill;
            this.txtResult.Location = new System.Drawing.Point(2, 2);
            this.txtResult.Margin = new System.Windows.Forms.Padding(2);
            this.txtResult.Multiline = true;
            this.txtResult.Name = "txtResult";
            this.txtResult.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.txtResult.Size = new System.Drawing.Size(983, 539);
            this.txtResult.TabIndex = 1;
            this.txtResult.TextChanged += new System.EventHandler(this.txtResult_TextChanged);
            // 
            // tblRank
            // 
            this.tblRank.Controls.Add(this.chkPerfect);
            this.tblRank.Controls.Add(this.lblInfo);
            this.tblRank.Controls.Add(this.splVis);
            this.tblRank.Controls.Add(this.lblOrigin);
            this.tblRank.Controls.Add(this.txtOriginal);
            this.tblRank.Controls.Add(this.lblSent);
            this.tblRank.Controls.Add(this.lstSentences);
            this.tblRank.Location = new System.Drawing.Point(4, 25);
            this.tblRank.Margin = new System.Windows.Forms.Padding(2);
            this.tblRank.Name = "tblRank";
            this.tblRank.Padding = new System.Windows.Forms.Padding(2);
            this.tblRank.Size = new System.Drawing.Size(987, 543);
            this.tblRank.TabIndex = 1;
            this.tblRank.Text = "Ranking";
            this.tblRank.UseVisualStyleBackColor = true;
            // 
            // chkPerfect
            // 
            this.chkPerfect.AutoSize = true;
            this.chkPerfect.Location = new System.Drawing.Point(7, 29);
            this.chkPerfect.Name = "chkPerfect";
            this.chkPerfect.Size = new System.Drawing.Size(139, 17);
            this.chkPerfect.TabIndex = 8;
            this.chkPerfect.Text = "Has Perfect Candidate?";
            this.chkPerfect.UseVisualStyleBackColor = true;
            // 
            // lblInfo
            // 
            this.lblInfo.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblInfo.Location = new System.Drawing.Point(155, 105);
            this.lblInfo.Name = "lblInfo";
            this.lblInfo.Size = new System.Drawing.Size(731, 30);
            this.lblInfo.TabIndex = 7;
            this.lblInfo.Text = "Double click to select/remove";
            // 
            // splVis
            // 
            this.splVis.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.splVis.Location = new System.Drawing.Point(158, 138);
            this.splVis.Name = "splVis";
            this.splVis.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // splVis.Panel1
            // 
            this.splVis.Panel1.Controls.Add(this.lstSelected);
            // 
            // splVis.Panel2
            // 
            this.splVis.Panel2.Controls.Add(this.lstRanking);
            this.splVis.Size = new System.Drawing.Size(836, 448);
            this.splVis.SplitterDistance = 123;
            this.splVis.TabIndex = 6;
            // 
            // lstSelected
            // 
            this.lstSelected.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.lstSelected.AutoScroll = true;
            this.lstSelected.Location = new System.Drawing.Point(4, 0);
            this.lstSelected.Name = "lstSelected";
            this.lstSelected.Size = new System.Drawing.Size(827, 89);
            this.lstSelected.TabIndex = 6;
            this.lstSelected.MouseEnter += new System.EventHandler(this.lstSelected_MouseEnter);
            this.lstSelected.Resize += new System.EventHandler(this.lstSelected_Resize);
            // 
            // lstRanking
            // 
            this.lstRanking.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.lstRanking.AutoScroll = true;
            this.lstRanking.Location = new System.Drawing.Point(3, 3);
            this.lstRanking.Name = "lstRanking";
            this.lstRanking.Size = new System.Drawing.Size(828, 312);
            this.lstRanking.TabIndex = 5;
            this.lstRanking.Paint += new System.Windows.Forms.PaintEventHandler(this.lstRanking_Paint);
            this.lstRanking.MouseEnter += new System.EventHandler(this.lstRanking_MouseEnter);
            this.lstRanking.PreviewKeyDown += new System.Windows.Forms.PreviewKeyDownEventHandler(this.lstRanking_PreviewKeyDown);
            this.lstRanking.Resize += new System.EventHandler(this.lstRanking_Resize);
            // 
            // lblOrigin
            // 
            this.lblOrigin.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.lblOrigin.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblOrigin.Location = new System.Drawing.Point(158, 2);
            this.lblOrigin.Name = "lblOrigin";
            this.lblOrigin.Size = new System.Drawing.Size(831, 24);
            this.lblOrigin.TabIndex = 3;
            this.lblOrigin.Text = "Original Sentence";
            // 
            // txtOriginal
            // 
            this.txtOriginal.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.txtOriginal.Font = new System.Drawing.Font("Lucida Sans Typewriter", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtOriginal.Location = new System.Drawing.Point(158, 29);
            this.txtOriginal.Name = "txtOriginal";
            this.txtOriginal.ReadOnly = true;
            this.txtOriginal.Size = new System.Drawing.Size(831, 73);
            this.txtOriginal.TabIndex = 2;
            this.txtOriginal.Text = "";
            // 
            // lblSent
            // 
            this.lblSent.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblSent.Location = new System.Drawing.Point(3, 2);
            this.lblSent.Name = "lblSent";
            this.lblSent.Size = new System.Drawing.Size(149, 24);
            this.lblSent.TabIndex = 1;
            this.lblSent.Text = "Sentences";
            // 
            // lstSentences
            // 
            this.lstSentences.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)));
            this.lstSentences.FormattingEnabled = true;
            this.lstSentences.Location = new System.Drawing.Point(3, 55);
            this.lstSentences.Name = "lstSentences";
            this.lstSentences.Size = new System.Drawing.Size(149, 524);
            this.lstSentences.TabIndex = 0;
            this.lstSentences.SelectedIndexChanged += new System.EventHandler(this.lstSentences_SelectedIndexChanged);
            // 
            // statusStrip1
            // 
            this.statusStrip1.Location = new System.Drawing.Point(0, 732);
            this.statusStrip1.Name = "statusStrip1";
            this.statusStrip1.Padding = new System.Windows.Forms.Padding(1, 0, 10, 0);
            this.statusStrip1.Size = new System.Drawing.Size(1116, 22);
            this.statusStrip1.TabIndex = 1;
            this.statusStrip1.Text = "statusStrip1";
            // 
            // frmTreeView
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1116, 754);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.mainSplitContainer);
            this.KeyPreview = true;
            this.Margin = new System.Windows.Forms.Padding(2);
            this.Name = "frmTreeView";
            this.Text = "Sentence Simp";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.frmTreeView_FormClosing);
            this.Load += new System.EventHandler(this.frmTreeView_Load);
            this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.frmTreeView_KeyDown);
            this.Resize += new System.EventHandler(this.frmTreeView_Resize);
            this.mainSplitContainer.Panel1.ResumeLayout(false);
            this.mainSplitContainer.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.mainSplitContainer)).EndInit();
            this.mainSplitContainer.ResumeLayout(false);
            this.leftFlowControl.ResumeLayout(false);
            this.leftFlowControl.PerformLayout();
            this.rightSplitContainer.Panel1.ResumeLayout(false);
            this.rightSplitContainer.Panel1.PerformLayout();
            this.rightSplitContainer.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.rightSplitContainer)).EndInit();
            this.rightSplitContainer.ResumeLayout(false);
            this.tabOutput.ResumeLayout(false);
            this.txtOutput.ResumeLayout(false);
            this.txtOutput.PerformLayout();
            this.tblRank.ResumeLayout(false);
            this.tblRank.PerformLayout();
            this.splVis.Panel1.ResumeLayout(false);
            this.splVis.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splVis)).EndInit();
            this.splVis.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.SplitContainer mainSplitContainer;
        private System.Windows.Forms.FlowLayoutPanel leftFlowControl;
        private System.Windows.Forms.StatusStrip statusStrip1;
        private System.Windows.Forms.Button btnTestSentenceIO;
        private System.Windows.Forms.Button btnOpenFile;
        private System.Windows.Forms.OpenFileDialog dlgOpenDialog;
        private System.Windows.Forms.Button btnTestParseTreeInput;
        private System.Windows.Forms.Button btnTestInput;
        private System.Windows.Forms.Button btnInitRanking;
        private System.Windows.Forms.ToolTip tipSentenceOrder;
        private System.Windows.Forms.SaveFileDialog dlgSave;
        private System.Windows.Forms.Button btnSaveRank;
        private System.Windows.Forms.Button btnOpenStream;
        private System.Windows.Forms.Button btnSplit;
        private System.Windows.Forms.CheckBox cbNoSave;
        private System.Windows.Forms.Button btnhs;
        private System.Windows.Forms.SplitContainer rightSplitContainer;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox txtInput;
        private System.Windows.Forms.TabControl tabOutput;
        private System.Windows.Forms.TabPage txtOutput;
        private System.Windows.Forms.TextBox txtResult;
        private System.Windows.Forms.TabPage tblRank;
        private System.Windows.Forms.SplitContainer splVis;
        private System.Windows.Forms.FlowLayoutPanel lstRanking;
        private System.Windows.Forms.Label lblSent;
        private System.Windows.Forms.ListBox lstSentences;
        private System.Windows.Forms.CheckBox chkPerfect;
        private System.Windows.Forms.Label lblInfo;
        private System.Windows.Forms.FlowLayoutPanel lstSelected;
        private System.Windows.Forms.Label lblOrigin;
        private System.Windows.Forms.RichTextBox txtOriginal;
    }
}

