import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class for ranking and unranking a permutation of types. We should be able to calculate the maximum potential
 * rank for the set and then find specific ranks for a series.
 */
public class FindMultisetRank
{
   /**
    * The is the FindRank constructor, each instance is initialize for permutation specific sets.
    * 
    * Example: FindRank m = new FindRank(new byte[] {0, 0, 1, 1, 2, 3});
    * 
    * @param baseSet The expected ordered multiset. Set of members, including repetitions.
    */
    public FindMultisetRank(byte[] baseSet)
    {
       m_length = baseSet.length;
       int last = 0;
       m_types = 1;
       for (int i = 0; i < baseSet.length; i++)
       {
          if (last != baseSet[i])
          {
             last = baseSet[i];
             m_types++;
          }
       }

       //count each type
       m_typeCount = new int[m_types];
       last = baseSet[0];
       for (int i = 0; i < baseSet.length; i++)
       {
          int type = baseSet[i];
          if (last != baseSet[i])
          {
             last = baseSet[i];
          }
          m_typeCount[type]++;
          if (m_maxTypeLength < m_typeCount[type]) m_maxTypeLength = m_typeCount[type];
       }

       computePotential();
   }

   /**
    * This method will return the permutation of the multiset on a given index.
    * 
    * @param permutationIndex The index of the permutation.
    * @return Returns the multiset.
    * @throws Exception Thrown if the index is beyond the permutation bounds. 
    */
   public byte[] unRank(int permutationIndex) throws Exception
   {
      if (permutationIndex < 0 || permutationIndex >= m_maxPotential)
         throw new Exception("Index is beyond permutation bounds.");
   
      byte[] result = new byte[m_length];
      int[] currentCounts = (int[])m_typeCount.clone();
      int currentPotential = m_maxPotential;
      int currentLength = m_length;

      // For each position...
      for (int position = 0; position < m_length; position++, currentLength--)
      {
         // Compute selector, which is just rank reduced to be in range of current length of multiset.
         int selector = ((permutationIndex * currentLength) / currentPotential);
         int offset = 0;
         byte type = 0;

         // Note that: 
         //  - Sum of count of all currenttly remaining types is length of multiset.
         //  - Current potential is sum of potentials of sub-multisets.
         // Scan for offset of sub-multiset in which range selector could be found.
         while ((offset + currentCounts[type]) <= selector)
         {
            offset += currentCounts[type];
            type++;
         }

         // Remove consumed offset.
         permutationIndex -= (currentPotential * offset) / currentLength;
         // Compute potential of sub-multiset.
         currentPotential = currentPotential * currentCounts[type] / currentLength;
         // Consume type.
         currentCounts[type]--;
         // Store chosen type.
         result[position] = type;
      }

      return result;
   }

   /**
    * Returns the rank of the permutation.
    * 
    * Example:
    * int pemutationIndex = m.findRank(new byte[] {0, 1, 0, 1, 2, 3);
    * 
    * @param multiset Any valid permutation of multiset that was initialized.
    * @return Returns the rank of the permutation.
    * @throws Exception Thrown if lengths are mismatched.
    */
   public int findRank(byte[] multiset) throws Exception
   {
      if (multiset.length != m_length)
         throw new Exception();

      int result = 0;
      int currentPotential = m_maxPotential;
      int currentLength = m_length;
      int[] currentCounts = (int[])m_typeCount.clone();

      // for each position
      for (int position = 0; position < m_length - 1 && currentPotential > 1; position++, currentLength--)
      {
          int offset = 0;
          byte type = (multiset[position]);

          // computing sum of potentials for each sub-multiset which has lower index than selected type
          for (int i = 0; i < type; i++)
          {
             offset += currentCounts[i];
          }

          // add offset to the rank/result
          result += (currentPotential * offset) / currentLength;
          // compute potential of sub-multiset
          currentPotential *= currentCounts[type];
          currentPotential /= currentLength;
          // consume type
          currentCounts[type]--;
      }

      return result;
   }

   public int getMaxPotential()
   {
      return m_maxPotential;
   }

   /**
    * An iterative function to find the factorial of n for the given size.
    * Returns one if n is less than or equal to 1.
    *
    * @param n The size of the set.
    * @return Returns the calculated factorial.
    */
   private static long findFactorial(int n)
   {
      int fact = 1;
      for (int i = 2; i <= n; i++)
      {
         fact *= i;
      }

      return fact;
   }

   /**
    * Computes the potential rank.
    * 
    *                             factorial(len)
    *                          --------div---------
    * (factorial(inTypes[0]) * factorial(inTypes[1]) * .. * factorial(typesCount-1))
    */
   private void computePotential()
   {
      long res = findFactorial(m_length);
      for (int t = 0; t < m_types; t++)
      {
         res /= findFactorial(m_typeCount[t]);
      }
      m_maxPotential = (int)res;
   }

   //////////////////////
   // Member Variable(s).
   //////////////////////

   /** The type count. */
   private int[] m_typeCount;

   /** The types in the permutation. */
   private int m_types;

   /** The initial length. */
   private int m_length;

   /** The maximum potential rank. */
   private int m_maxPotential;

   /** The maximum type length. */
   private int m_maxTypeLength;

   //////////////////////
   // Main Method.
   //////////////////////

   public static void main(final String[] args) throws Exception
   {
      // This should also work for character arrays converted into byte arrays.
      // Example:
      /*
      char[] charArray = "aabbcd".toCharArray();
      byte[] byteArray = new byte[charArray.length];
      ByteBuffer.wrap(byteArray).asCharBuffer().put(charArray);
      FindMultisetRank m = new FindMultisetRank(byteArray);
      */
      
      FindMultisetRank m = new FindMultisetRank(new byte[] {0, 0, 1, 1, 2, 3});
      System.out.println("The Maximum potential rank is: " + m.getMaxPotential());

      for(int i = 0; i < m.getMaxPotential(); i++)
      {
         byte[] perm = m.unRank(i);
         int j = m.findRank(perm);
         String string = new String(Arrays.toString(perm));
         System.out.println("Start: " + i + " Permutation: " + string);
      }
   }
}
