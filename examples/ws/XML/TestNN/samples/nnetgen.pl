#!/usr/bin/perl

#Quick perl to create a random NNet XML file

use strict;
use Getopt::Long;

sub usage() {
  print STDERR "usage: $0 -f <file> i [h1 ... hn] o\n";
  print STDERR "where i, h1, ..., hn, o are #of neurons\n";
  exit(1);
}

my $file;

#complete nnet
#my @nnet;

#layers
my @layers;

GetOptions('f=s' => \$file);

#usage() if !defined $file;

my $tmp;
while($tmp = shift) {
  push(@layers, $tmp);
}

local *OUT;

if(!defined $file) {
  *OUT = *STDOUT;
}
else {
  open(OUT, ">$file");
}

print OUT "<?xml version=\"1.0\"?>\n<net>\n";
print OUT "\n<!-- NNet XML created with $0 -->\n\n";
for(my $i=1; $i<=scalar(@layers); $i++) {

  print OUT "\t<layer type=\"";

  if($i == 1) {
    print OUT "input";
  } elsif($i == scalar(@layers)) {
    print OUT "output";
  } else {
    print OUT "hidden";
  }

  print OUT "\" index=\"$i\">\n";

  for(my $j=1; $j<=$layers[$i-1]; $j++) {

    print OUT "\t\t<neuron index=\"$j\" thresh=\"1.0\">\n";

    if($i > 1) { #we have inputs!
      for(my $k=1; $k<=$layers[$i-2]; $k++) {
	my $tmp = rand(2.0) - 1.0;
	print OUT "\t\t\t<input ref=\"$k\" weight=\"$tmp\"/>\n";
      }
    }
    if($i < scalar(@layers)) { #we have outputs!
      for(my $k=1; $k<=$layers[$i]; $k++) {
	print OUT "\t\t\t<output ref=\"$k\"/>\n";
      }
    }

    print OUT "\t\t</neuron>\n";
  }

  print OUT "\t</layer>\n";

}

print OUT "</net>\n";
