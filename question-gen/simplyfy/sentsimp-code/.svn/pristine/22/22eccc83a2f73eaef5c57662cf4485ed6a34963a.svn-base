#!/afs/cs.cmu.edu/user/nbach/ActivePerl-5.8.8/bin/perl
use strict;
use warnings;
use Net::Amazon::MechanicalTurk;
use Net::Amazon::MechanicalTurk::XMLParser;
use Net::Amazon::MechanicalTurk::IOUtil;
use Net::Amazon::MechanicalTurk::Command::ParseAssignmentAnswer;
use XML::Simple;

#
# This sample script displays answers from the hit
# created in helloworld-create.pl.
#
# This sample demonstrates the following features:
#
#  1. Using the GetAssignmentsForHITAll method for iterating all assignments.
#  2. Using the XMLParser to get information out of the Answer XML.
#  3. Using the toString metod to see what is in a response
#      or parsed XML document.
#  4. Using the ApproveAssignment method.
#

# Read the hitid from the text file
#sub getHITId {
#    my ($file) = @_;
#    my $hitid = Net::Amazon::MechanicalTurk::IOUtil->readContents($file);
#    chomp($hitid);
#    return $hitid;
#}

if (@ARGV < 1) {
	print "$0 hitId-file\n";
	exit;
}

my $mturk = Net::Amazon::MechanicalTurk->new;

# Create an XML parser to go through the Answer
my $parser = Net::Amazon::MechanicalTurk::XMLParser->new;

my ($totalHIT, $doneHIT) = (0, 0);

# look up assignments for the hit
open HITID,"< $ARGV[0]" or die "Cannot open $ARGV[0]\n";

print "AssignmentId\tAssignmentStatus\tHITID\tWorkerId\tSS\tTime\tLang\tCity\tRegion\tCountry\n";

while (<HITID>){
	my ($hitId, $groupID, $link) = split"\t";
	$totalHIT++;

	my $assignments = $mturk->GetAssignmentsForHITAll( HITId => $hitId );
	
	while (my $assignment = $assignments->next) {
		# If you want to see a dump of what is in a response object from an
	    # API call, you can use the toString method.
		#print $assignment->toString, "\n";
		
		print STDERR "Getting results of HITID $hitId ...\n";

	    my $workerId = $assignment->{WorkerId}[0];
    
	    # Parse the answer XML - The answer object returned also has a toString method
		#my $answer = $parser->parse($assignment->{Answer}[0]);
		#print $answer->toString, "\n";
    
		#my $answerText = $answer->{Answer}[0]{FreeText}[0];
    
		#printf "Worker %s said \"%s\"\n", $workerId, $answerText;

		my ($ss, $lang, $city, $country, $time, $region);

		#print "Scalar = ". scalar @{$answer->{Answer}}. "\n";	

		my $answers = $mturk->parseAssignmentAnswer($assignment); 
		$answers->eachAnswerValue(sub { 
			my ($questionId, $answerText) = @_; 
			#printf("%s = %s\n", $questionId, $answerText); 
		
			if    ( $questionId eq "lang" )    { $lang    = $answerText; }
			elsif ( $questionId eq "time"   )  { $time    = $answerText; }
			elsif ( $questionId eq "region" )  { $region  = $answerText; }
			elsif ( $questionId eq "country" ) { $country = $answerText; }
			elsif ( $questionId eq "ss" )      { $ss      = $answerText; }
			elsif ( $questionId eq "city" )    { $city    = $answerText; }	
		});

		print $assignment->{AssignmentId}[0]."\t".$assignment->{AssignmentStatus}[0]."\t$hitId\t$workerId\t$ss\t$time\t$lang\t$city\t$region\t$country\n";

		$doneHIT++;
		#if ($assignment->{AssignmentStatus}[0] eq "Submitted") {
		#    print "Approving the assignment.\n";
		#    $mturk->ApproveAssignment( AssignmentId => $assignment->{AssignmentId}[0] );
		#}
	}
}
close HITID;
print STDERR "Total HITs: $totalHIT ; Submitted HITs: $doneHIT\n";


