package com.developcollect.extra.media.gif;

/**
 * 多张静态图片合成动态gif图工具类
 */
@SuppressWarnings("ALL")
public class NeuQuant {
    /* number of colours used */
    /* four primes near 500 - assume no image has a length so large */
    /* that it is divisible by all four primes */
    protected static final int NETSIZE = 256;
    protected static final int PRIME_1 = 499;
    protected static final int PRIME_2 = 491;
    protected static final int PRIME_3 = 487;
    protected static final int PRIME_4 = 503;
    protected static final int MINPICTUREBYTES = (3 * PRIME_4);
    /* minimum size for input image */
    /*
     * Program Skeleton ---------------- [select samplefac in range 1..30] [read
     * image from input file] pic = (unsigned char*) malloc(3*width*height);
     * initnet(pic,3*width*height,samplefac); learn(); unbiasnet(); [write
     * output image header, using writecolourmap(f)] inxbuild(); write output
     * image using inxsearch(b,g,r)
     */
    /*
     * Network Definitions -------------------
     */
    protected static final int MAXNETPOS = (NETSIZE - 1);
    /* bias for colour values */
    protected static final int NETBIASSHIFT = 4;
    /* no. of learning cycles */
    protected static final int NCYCLES = 100;
    /* defs for freq and bias */
    /* bias for fractions */
    protected static final int INTBIASSHIFT = 16;
    protected static final int INTBIAS = (((int) 1) << INTBIASSHIFT);
    /* GAMMA = 1024 */
    protected static final int GAMMASHIFT = 10;
    protected static final int GAMMA = (((int) 1) << GAMMASHIFT);
    protected static final int BETASHIFT = 10;
    protected static final int BETA = (INTBIAS >> BETASHIFT); /*
     * BETA = 1/1024
     */
    protected static final int BETAGAMMA = (INTBIAS << (GAMMASHIFT - BETASHIFT));
    /* defs for decreasing radius factor */
    protected static final int INITRAD = (NETSIZE >> 3); /*
     * for 256 cols, radius
     * starts
     */
    protected static final int RADIUSBIASSHIFT = 6; /*
     * at 32.0 biased by 6 bits
     */
    protected static final int RADIUSBIAS = (((int) 1) << RADIUSBIASSHIFT);
    /* and decreases by a */
    protected static final int INITRADIUS = (INITRAD
            * RADIUSBIAS);
    /* factor of 1/30 each cycle */
    protected static final int RADIUSDEC = 30;
    /* defs for decreasing alpha factor */
    /* alpha starts at 1.0 */
    protected static final int ALPHABIASSHIFT = 10;
    protected static final int INITALPHA = (((int) 1) << ALPHABIASSHIFT);
    /* biased by 10 bits */
    protected int alphadec;
    /* RADBIAS and ALPHA_RADBIAS used for radpower calculation */
    protected static final int RADBIASSHIFT = 8;
    protected static final int RADBIAS = (((int) 1) << RADBIASSHIFT);
    protected static final int ALPHARADBSHIFT = (ALPHABIASSHIFT + RADBIASSHIFT);
    protected static final int ALPHA_RADBIAS = (((int) 1) << ALPHARADBSHIFT);
    /*
     * Types and Global Variables --------------------------
     */
    /* the input image itself */
    protected byte[] thepicture;
    /* lengthcount = H*W*3 */
    protected int lengthcount;
    /* sampling factor 1..30 */
    protected int samplefac;
    // typedef int pixel[4]; /* BGRc */
    /* the network itself - [NETSIZE][4] */
    protected int[][] network;
    protected int[] netindex = new int[256];
    /* for network lookup - really 256 */
    protected int[] bias = new int[NETSIZE];
    /* bias and freq arrays for learning */
    protected int[] freq = new int[NETSIZE];
    protected int[] radpower = new int[INITRAD];

    /* radpower for precomputation */
    /*
     * Initialise network in range (0,0,0) to (255,255,255) and set parameters
     * -----------------------------------------------------------------------
     */
    public NeuQuant(byte[] thepic, int len, int sample) {
        int i;
        int[] p;
        thepicture = thepic;
        lengthcount = len;
        samplefac = sample;
        network = new int[NETSIZE][];
        for (i = 0; i < NETSIZE; i++) {
            network[i] = new int[4];
            p = network[i];
            p[0] = p[1] = p[2] = (i << (NETBIASSHIFT + 8)) / NETSIZE;
            /* 1/NETSIZE */
            freq[i] = INTBIAS / NETSIZE;
            bias[i] = 0;
        }
    }

    public byte[] colorMap() {
        byte[] map = new byte[3 * NETSIZE];
        int[] index = new int[NETSIZE];
        for (int i = 0; i < NETSIZE; i++) {
            index[network[i][3]] = i;
        }
        int k = 0;
        for (int i = 0; i < NETSIZE; i++) {
            int j = index[i];
            map[k++] = (byte) (network[j][0]);
            map[k++] = (byte) (network[j][1]);
            map[k++] = (byte) (network[j][2]);
        }
        return map;
    }

    /*
     * Insertion sort of network and building of netindex[0..255] (to do after
     * unbias)
     * -------------------------------------------------------------------------
     * ------
     */
    public void inxbuild() {
        int i, j, smallpos, smallval;
        int[] p;
        int[] q;
        int previouscol, startpos;
        previouscol = 0;
        startpos = 0;
        for (i = 0; i < NETSIZE; i++) {
            p = network[i];
            smallpos = i;
            /* index on g */
            smallval = p[1];
            /* find smallest in i..NETSIZE-1 */
            for (j = i + 1; j < NETSIZE; j++) {
                q = network[j];
                /* index on g */
                if (q[1] < smallval) {
                    smallpos = j;
                    /* index on g */
                    smallval = q[1];
                }
            }
            q = network[smallpos];
            /* swap p (i) and q (smallpos) entries */
            if (i != smallpos) {
                j = q[0];
                q[0] = p[0];
                p[0] = j;
                j = q[1];
                q[1] = p[1];
                p[1] = j;
                j = q[2];
                q[2] = p[2];
                p[2] = j;
                j = q[3];
                q[3] = p[3];
                p[3] = j;
            }
            /* smallval entry is now in position i */
            if (smallval != previouscol) {
                netindex[previouscol] = (startpos + i) >> 1;
                for (j = previouscol + 1; j < smallval; j++) {
                    netindex[j] = i;
                }
                previouscol = smallval;
                startpos = i;
            }
        }
        netindex[previouscol] = (startpos + MAXNETPOS) >> 1;
        for (j = previouscol + 1; j < 256; j++) {
            /* really 256 */
            netindex[j] = MAXNETPOS;
        }
    }

    /*
     * Main Learning Loop ------------------
     */
    public void learn() {
        int i, j, b, g, r;
        int radius, rad, alpha, step, delta, samplepixels;
        byte[] p;
        int pix, lim;
        if (lengthcount < MINPICTUREBYTES) {
            samplefac = 1;
        }
        alphadec = 30 + ((samplefac - 1) / 3);
        p = thepicture;
        pix = 0;
        lim = lengthcount;
        samplepixels = lengthcount / (3 * samplefac);
        delta = samplepixels / NCYCLES;
        alpha = INITALPHA;
        radius = INITRADIUS;
        rad = radius >> RADIUSBIASSHIFT;
        if (rad <= 1) {
            rad = 0;
        }
        for (i = 0; i < rad; i++) {
            radpower[i] = alpha * (((rad * rad - i * i) * RADBIAS) / (rad * rad));
        }
        // fprintf(stderr,"beginning 1D learning: initial radius=%d/n", rad);
        if (lengthcount < MINPICTUREBYTES) {
            step = 3;
        } else if ((lengthcount % PRIME_1) != 0) {
            step = 3 * PRIME_1;
        } else {
            if ((lengthcount % PRIME_2) != 0) {
                step = 3 * PRIME_2;
            } else {
                if ((lengthcount % PRIME_3) != 0) {
                    step = 3 * PRIME_3;
                } else {
                    step = 3 * PRIME_4;
                }
            }
        }
        i = 0;
        while (i < samplepixels) {
            b = (p[pix + 0] & 0xff) << NETBIASSHIFT;
            g = (p[pix + 1] & 0xff) << NETBIASSHIFT;
            r = (p[pix + 2] & 0xff) << NETBIASSHIFT;
            j = contest(b, g, r);
            altersingle(alpha, j, b, g, r);
            if (rad != 0) {
                /* alter neighbours */
                alterneigh(rad, j, b, g, r);
            }
            pix += step;
            if (pix >= lim) {
                pix -= lengthcount;
            }
            i++;
            if (delta == 0) {
                delta = 1;
            }
            if (i % delta == 0) {
                alpha -= alpha / alphadec;
                radius -= radius / RADIUSDEC;
                rad = radius >> RADIUSBIASSHIFT;
                if (rad <= 1) {
                    rad = 0;
                }
                for (j = 0; j < rad; j++) {
                    radpower[j] = alpha * (((rad * rad - j * j) * RADBIAS) / (rad * rad));
                }
            }
        }
        // fprintf(stderr,"finished 1D learning: final alpha=%f
        // !/n",((float)alpha)/INITALPHA);
    }

    /*
     * Search for BGR values 0..255 (after net is unbiased) and return colour
     * index
     * -------------------------------------------------------------------------
     * ---
     */
    public int map(int b, int g, int r) {
        int i, j, dist, a, bestd;
        int[] p;
        int best;
        /* biggest possible dist is 256*3 */
        bestd = 1000;
        best = -1;
        /* index on g */
        i = netindex[g];
        /* start at netindex[g] and work outwards */
        j = i - 1;
        while ((i < NETSIZE) || (j >= 0)) {
            if (i < NETSIZE) {
                p = network[i];
                /* inx key */
                dist = p[1] - g;
                if (dist >= bestd) {
                    /* stop iter */
                    i = NETSIZE;
                } else {
                    i++;
                    if (dist < 0) {
                        dist = -dist;
                    }
                    a = p[0] - b;
                    if (a < 0) {
                        a = -a;
                    }
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0) {
                            a = -a;
                        }
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
            if (j >= 0) {
                p = network[j];
                /* inx key - reverse dif */
                dist = g - p[1];
                if (dist >= bestd) {
                    /* stop iter */
                    j = -1;
                } else {
                    j--;
                    if (dist < 0) {
                        dist = -dist;
                    }
                    a = p[0] - b;
                    if (a < 0) {
                        a = -a;
                    }
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0) {
                            a = -a;
                        }
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
        }
        return (best);
    }

    public byte[] process() {
        learn();
        unbiasnet();
        inxbuild();
        return colorMap();
    }

    /*
     * Unbias network to give byte values 0..255 and record position i to
     * prepare for sort
     * -------------------------------------------------------------------------
     * ----------
     */
    public void unbiasnet() {
        int i, j;
        for (i = 0; i < NETSIZE; i++) {
            network[i][0] >>= NETBIASSHIFT;
            network[i][1] >>= NETBIASSHIFT;
            network[i][2] >>= NETBIASSHIFT;
            /* record colour no */
            network[i][3] = i;
        }
    }

    /*
     * Move adjacent neurons by precomputed alpha*(1-((i-j)^2/[r]^2)) in
     * radpower[|i-j|]
     * -------------------------------------------------------------------------
     * --------
     */
    protected void alterneigh(int rad, int i, int b, int g, int r) {
        int j, k, lo, hi, a, m;
        int[] p;
        lo = i - rad;
        if (lo < -1) {
            lo = -1;
        }
        hi = i + rad;
        if (hi > NETSIZE) {
            hi = NETSIZE;
        }
        j = i + 1;
        k = i - 1;
        m = 1;
        while ((j < hi) || (k > lo)) {
            a = radpower[m++];
            if (j < hi) {
                p = network[j++];
                try {
                    p[0] -= (a * (p[0] - b)) / ALPHA_RADBIAS;
                    p[1] -= (a * (p[1] - g)) / ALPHA_RADBIAS;
                    p[2] -= (a * (p[2] - r)) / ALPHA_RADBIAS;
                } catch (Exception e) {
                } // prevents 1.3 miscompilation
            }
            if (k > lo) {
                p = network[k--];
                try {
                    p[0] -= (a * (p[0] - b)) / ALPHA_RADBIAS;
                    p[1] -= (a * (p[1] - g)) / ALPHA_RADBIAS;
                    p[2] -= (a * (p[2] - r)) / ALPHA_RADBIAS;
                } catch (Exception e) {
                }
            }
        }
    }

    /*
     * Move neuron i towards biased (b,g,r) by factor alpha
     * ----------------------------------------------------
     */
    protected void altersingle(int alpha, int i, int b, int g, int r) {
        /* alter hit neuron */
        int[] n = network[i];
        n[0] -= (alpha * (n[0] - b)) / INITALPHA;
        n[1] -= (alpha * (n[1] - g)) / INITALPHA;
        n[2] -= (alpha * (n[2] - r)) / INITALPHA;
    }

    /*
     * Search for biased BGR values ----------------------------
     */
    protected int contest(int b, int g, int r) {
        /* finds closest neuron (min dist) and updates freq */
        /* finds best neuron (min dist-bias) and returns position */
        /*
         * for frequently chosen neurons, freq[i] is high and bias[i] is
         * negative
         */
        /* bias[i] = GAMMA*((1/NETSIZE)-freq[i]) */
        int i, dist, a, biasdist, BETAfreq;
        int bestpos, bestbiaspos, bestd, bestbiasd;
        int[] n;
        bestd = ~(((int) 1) << 31);
        bestbiasd = bestd;
        bestpos = -1;
        bestbiaspos = bestpos;
        for (i = 0; i < NETSIZE; i++) {
            n = network[i];
            dist = n[0] - b;
            if (dist < 0) {
                dist = -dist;
            }
            a = n[1] - g;
            if (a < 0) {
                a = -a;
            }
            dist += a;
            a = n[2] - r;
            if (a < 0) {
                a = -a;
            }
            dist += a;
            if (dist < bestd) {
                bestd = dist;
                bestpos = i;
            }
            biasdist = dist - ((bias[i]) >> (INTBIASSHIFT - NETBIASSHIFT));
            if (biasdist < bestbiasd) {
                bestbiasd = biasdist;
                bestbiaspos = i;
            }
            BETAfreq = (freq[i] >> BETASHIFT);
            freq[i] -= BETAfreq;
            bias[i] += (BETAfreq << GAMMASHIFT);
        }
        freq[bestpos] += BETA;
        bias[bestpos] -= BETAGAMMA;
        return (bestbiaspos);
    }
}
