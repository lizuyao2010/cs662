#!/usr/bin/perl
use strict;
use warnings;
use Net::Amazon::MechanicalTurk;
use URI::Escape;
#
# This sample uses the method loadHITs for bulk loading many hits.
# Data is read from a CSV input file
# which is merged with the question template to produce the xml for the question.
# Each row corresponds to a HIT.
# Progress messages will be printed to the console.
# Successful HITId and HITTypeId's will be printed to a CSV success file.
# Failed rows from the input file will be printed to a CSV failure file.
#

sub questionTemplate {
    my %params = %{$_[0]};
	my $s = uri_escape($params{sentence});
    return <<END_XML;
<?xml version="1.0" encoding="UTF-8"?>
<ExternalQuestion xmlns="http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2006-07-14/ExternalQuestion.xsd">
        <ExternalURL>http://sentsimp.appspot.com/?sent=$s</ExternalURL>
        <FrameHeight>400</FrameHeight>
</ExternalQuestion>
END_XML
}

my $properties = {
    Title       => 'Sentence Simplification',
    Description => 'This is a test of the bulk loading API.',
    Keywords    => 'LoadHITs, bulkload, perl, unique1',
    Reward => {
        CurrencyCode => 'USD',
        Amount       => 0.00
    },
    RequesterAnnotation         => 'test',
    AssignmentDurationInSeconds => 60 * 60,
    AutoApprovalDelayInSeconds  => 60 * 60 * 10,
    MaxAssignments              => 3,
    LifetimeInSeconds           => 60 * 60
};

my $mturk = Net::Amazon::MechanicalTurk->new();

$mturk->loadHITs(
    properties => $properties,
    input      => "loadhits-input.csv",
    question   => \&questionTemplate,
    progress   => \*STDOUT,
    success    => "loadhits-success.csv",
    fail       => "loadhits-failure.csv"
);

