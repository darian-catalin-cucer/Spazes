package com.mcdev.spazes.adapter

//class SpacesAdapter(private val context: Context, private val response: SpacesResponse): RecyclerView.Adapter<SpacesAdapter.ViewHolder>() {
//
//
//    // Create new views (invoked by the layout manager)
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        // Create a new view, which defines the UI of the list item
////        val view = LayoutInflater.from(parent.context).inflate(R.layout.space_item, parent ,false)
//        val binding: SpaceItemBinding = SpaceItemBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.space_item, parent ,false))
//
//        return ViewHolder(binding)
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        // Get element from your dataset at this position and replace the
//        // contents of the view with that element
//
//        when (response.data?.get(position)?.state) {
//            SpacesState.LIVE.value -> {
//                holder.binding.liveViews.visibility = View.VISIBLE
//                holder.binding.stateScheduledView.visibility = View.GONE
//                holder.binding.participantCount.text = response.data?.get(position)?.participantCount?.inc().toString()
//                holder.binding.lotTv.text = context.getString(R.string.listen_on_twitter)
//            }
//            SpacesState.SCHEDULED.value -> {
//                holder.binding.stateScheduledView.visibility = View.VISIBLE
//                holder.binding.liveViews.visibility = View.GONE
//
//                //set reminder
//                holder.binding.lotTv.text = context.getString(R.string.set_reminder)
//                holder.binding.stateScheduledView.text = response.data?.get(position)?.scheduledStart?.formatDateAndTime()
//            }
//        }
//
//
//        val creatorid: String = response.data?.get(position)?.creatorId!!
//        val creator : User? = response.includes?.users?.find {
//            it.id == creatorid
//        }
//
//        val title = response.data?.get(position)?.title
//        if (!title.isNullOrBlank()) {
//            holder.binding.title.text = title
//        } else {
//            holder.binding.title.text = "${creator?.name}'s Space"
//        }
//        holder.binding.participants.text = creator?.name
//        holder.binding.speakerAvi.setImageURI(creator?.profileImageUrl)
//
//        val hostIds = response.data?.get(position)?.hostIds
//
//        holder.binding.lotLay.setOnClickListener {
//            val link = SPACES_URL+response.data?.get(position)?.id
//            Log.d("TAG", "onBindViewHolder: link is : $link")
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
//            context.startActivity(intent)
//        }
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount(): Int {
//        return response.meta!!.resultCount
//    }
//
//    /**
//     * Provides a reference to the type of views that you are using
//     * (custom ViewHolder).
//     */
//    class ViewHolder(bind: SpaceItemBinding) : RecyclerView.ViewHolder(bind.root) {
//        var binding = bind
//    }
//}