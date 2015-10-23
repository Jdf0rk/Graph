#!/bin/bash
for a in {1..400}
do
i=$(((RANDOM*5)%150827))
j=$(((RANDOM*5)%150827))
java -Xmx1g -jar begraphes.jar midip 0 2 $i $j 0 >> midip_dist_normal
java -Xmx1g -jar begraphes.jar midip 0 3 $i $j 0 >> midip_dist_star
done
