#!/usr/bin/perl 
#===============================================================================
#
#         FILE:  AnalysisChunnk.pl
#
#        USAGE:  ./AnalysisChunnk.pl 
#
#  DESCRIPTION:  
#
#      OPTIONS:  ---
# REQUIREMENTS:  ---
#         BUGS:  ---
#        NOTES:  ---
#       AUTHOR:   (), <>
#      COMPANY:  
#      VERSION:  1.0
#      CREATED:  02/07/11 12:40:23 EST
#     REVISION:  ---
#===============================================================================

use strict;
use warnings;


my %enNP;
open (ENP, $ARGV[0]) or die "Cannot open $ARGV[0]\n";
while (<ENP>){
	chomp;
	my ($id, $sent) = split /\s*\t\s*/, $_;
    while ( $sent =~ m/\[\s*(.+?)\s*\]/ ){ 
		#print $1."\n"; 
		
		push @{ $enNP{$id} } , $1;

		#print "DEBUG $id  $1\n";

		$sent =~ s/\[(.+?)\]/$1/;
	}
}
close ENP;

my %simpNP;
open (SNP, $ARGV[1]) or die "Cannot open $ARGV[1]\n";
my ($m, $nf, $msent1) = (0, 0, 0);
while (<SNP>){
	chomp;
	my ($id, $np) = split /\s*\t\s*/, $_;

	$np =~ s/^\s*//g;
	$np =~ s/\s*$//g;

	my @w = split /\-/, $id;
	my $sid = "Sent-". $w[1];	

	print "$sid\tnp=$np\tnp1=$enNP{$sid}[0]\t";
	if ($np eq $enNP{$sid}[0]) {
		print"Match NP1\n";
		$m++;

		$msent1++  if ($w[2] == 1);

	}else{
		print "NOT FOUND\n";
		$nf++;
	}

}
close SNP;

printf ("Found = %d (%.3f)\nNot Found = %d (%.3f)\n", $m, $m/($m+$nf), $nf, $nf/($m+$nf));
printf ("Found in the 1st simplified sentence = %d (%.3f)\n", $msent1, $msent1/$m);

#foreach my $k ( sort keys %enNP){
#	for( my $i=0; $i < $#{ $enNP{$k} } ; $i++ ){
#		print "$k\t$i\t$enNP{$k}[$i]\n";
#	}
#}