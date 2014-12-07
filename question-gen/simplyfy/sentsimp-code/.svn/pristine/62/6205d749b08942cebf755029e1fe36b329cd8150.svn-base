#!/afs/cs.cmu.edu/user/nbach/ActivePerl-5.8.8/bin/perl
use strict;
use warnings;
use IO::File;
use Net::Amazon::MechanicalTurk;
use Net::Amazon::MechanicalTurk::Properties;
use Net::Amazon::MechanicalTurk::IOUtil;
use URI::Escape;

if (@ARGV <2){
	print "$0 sentence-list delay-time\n"; 
	exit;
}

my $delaySeconds = $ARGV[1];

my $hitIDfile = "hitID.txt";

my @sentenceList = `cat $ARGV[0]`;

my $errorSubmissionSentence = "";
my $errorNumber = 0;

`rm -f hitID.txt`;
open HIT, ">$hitIDfile" or die "Cannot open $hitIDfile file\n";

foreach my $s (@sentenceList ){
	chomp($s);
	my $encode = uri_escape($s);
	&submit($encode);
	print STDERR "Delay $delaySeconds seconds for the next HIT submission ...\n";
	sleep($delaySeconds);
}
close HIT;
print STDERR "Error submission: ". $errorNumber. "\n";
print STDERR "Error list \n". $errorSubmissionSentence ."\n";

####
# Submit a HIT
#
sub submit(){

my $question = shift;

my $questionXml = <<END_XML;
<?xml version="1.0" encoding="UTF-8"?>
<ExternalQuestion xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd"> 
	<ExternalURL>http://sentsimp.appspot.com/?sent=$question</ExternalURL> 
	<FrameHeight>600</FrameHeight> 
</ExternalQuestion>
END_XML

my $mturk = Net::Amazon::MechanicalTurk->new;

my $result;

# Read the properties into a nested data structure
my $hitProperties = Net::Amazon::MechanicalTurk::Properties->readNestedData(
     "SentSimp.properties"
);
# Put the question into the hitProperties
$hitProperties->{Question} = $questionXml;

eval{
 $result = $mturk->CreateHIT(
	 $hitProperties
#    Title       => 'Rewrite an English sentence into shorter ones',
#    Description => 'Sentence simplification',
#    Keywords    => 'English, word, sentence, simplification',
#    Reward => {
#        CurrencyCode => 'USD',
#        Amount       => 0.01
#    },
#    RequesterAnnotation         => 'Test Hit',
#    AssignmentDurationInSeconds => 60 * 60,
#    AutoApprovalDelayInSeconds  => 60 * 60 * 10,
#    MaxAssignments              => 1,
#    LifetimeInSeconds           => 60 * 60,
#    Question                    => $questionXml
 );
};
if ($@) {
	print "Error: \n". $@ . "\n"; 
	$errorSubmissionSentence .= uri_unescape($question) ."\n";
	$errorNumber ++;
}else {
	print STDERR "Created HIT:\n";
	print STDERR "HITId:     ". $result->{HITId}[0]     ."\n";
	print STDERR "HITTypeId: ". $result->{HITTypeId}[0] ."\n";

	print STDERR "\nYou may see your hit here: ". $mturk->getHITTypeURL($result->{HITTypeId}[0])."\n";

	#hitID, hitIdType, hitLink
	print HIT $result->{HITId}[0]. "\t" . $result->{HITTypeId}[0] . "\t" . $mturk->getHITTypeURL($result->{HITTypeId}[0]). "\n";
}

# Write out the HITId to a text file in order to get 
# the answer in the helloworld-answer.pl script.
#Net::Amazon::MechanicalTurk::IOUtil->writeContents(
#    "hitid.txt", $result->{HITId}[0]
#);

}
