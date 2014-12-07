#!/afs/cs.cmu.edu/user/nbach/ActivePerl-5.8.8/bin/perl
use strict;
use warnings;
use Net::Amazon::MechanicalTurk;
use Net::Amazon::MechanicalTurk::RowData;

#
# This sample reads the results file and tries to approve all assignments
# that do not have a reject column with an X.
#
# This sample demonstrates:
#
# 1. Using the RowData class to iterate through a delimited file.
# 2. Approving assignments.
# 3. How to examine the error code from a failed call.
#
# Note: RowData may be used on csv or tab delimited files.
#   The rows are iterated over in the code below, the contents of
#   the file is not read completely into memory.
#

if (@ARGV < 1) {
	print "$0 hitId-result-file\n";
	exit;
}
				
my $mturk = Net::Amazon::MechanicalTurk->new;
my $data  = Net::Amazon::MechanicalTurk::RowData->toRowData($ARGV[0]);

$data->each(sub {
    my ($data, $row) = @_;
    my $assignmentId = $row->{AssignmentId};
    
    if (!(exists $row->{Reject} and lc($row->{Reject}) eq "x")) {
        eval {
            $mturk->ApproveAssignment( AssignmentId => $assignmentId );
            printf "Approved assignment %s\n", $assignmentId;
        };
        if ($@) {
            if ($mturk->response->errorCode eq "AWS.MechanicalTurk.InvalidAssignmentState") {
                print "Assignment $assignmentId has already been processed.\n";
            }
            else {
                die $@;
            }
        }
    }elsif (exists $row->{Reject} and lc($row->{Reject}) eq 'x') { #reject if the row is marked  "x"
		print "Rejecting assignment $assignmentId\n";
		eval {
			$mturk->RejectAssignment( AssignmentId => $assignmentId );
		};
		if ($@) {
			if ($mturk->response->errorCode eq "AWS.MechanicalTurk.InvalidAssignmentState") {
				print "Assignment $assignmentId has already been processed.\n";
			} else { die $@; }
		}
	}
																																				
});
